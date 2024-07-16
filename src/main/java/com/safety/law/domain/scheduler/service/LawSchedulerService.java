package com.safety.law.domain.scheduler.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.domain.scheduler.constant.SchedulerConstant;
import com.safety.law.domain.scheduler.constant.StatusConstant;
import com.safety.law.domain.scheduler.model.LawRS;
import com.safety.law.domain.scheduler.model.LawRS.Item;
import com.safety.law.global.external.PublicDataPotalComponent;
import com.safety.law.global.jpa.entity.LawEntity;
import com.safety.law.global.jpa.entity.LawUpdateHistoryEntity;
import com.safety.law.global.jpa.entity.SchedulerUpdateDateEntity;
import com.safety.law.global.jpa.repository.LawRepository;
import com.safety.law.global.jpa.repository.LawUpdateHistoryRepository;
import com.safety.law.global.jpa.repository.SchedulerUpdateDateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LawSchedulerService {

    
    private final LawRepository lawRepository;

    private final LawUpdateHistoryRepository lawUpdateHistoryRepository;
    
    private final SchedulerUpdateDateRepository schedulerUpdateDateRepository;
    
    private final PublicDataPotalComponent potalComponent;

    @Value("${data.portal.law-key}")
    private String DATA_LAW_KEY;

    private final Integer DATA_SIZE = 30;

    /**
     * 공공데이터 포탈의 수정일을 크롤링하여 기존 수정일과 다를 경우 데이터를 수집함
     * @throws Exception
     */
    public void lawDataScheduler() throws Exception{
  
        String lawUpdatDate = this.getLawUpdatDate();

        Optional<SchedulerUpdateDateEntity> schedulerUpdateDateWrapper = schedulerUpdateDateRepository.findById(SchedulerConstant.LAW_DATA_PORTAL_UPDATE_DATE.name());

        if(schedulerUpdateDateWrapper.isPresent()){
            SchedulerUpdateDateEntity entity = schedulerUpdateDateWrapper.get();

            if(!entity.getUpdateDt().isEqual(LocalDate.parse(lawUpdatDate))){

                entity = SchedulerUpdateDateEntity.builder()
                .name(SchedulerConstant.LAW_DATA_PORTAL_UPDATE_DATE.name())
                .updateDt(LocalDate.parse(lawUpdatDate))
                .status(StatusConstant.PROCEEDING.name())
                .build();

                schedulerUpdateDateRepository.save(entity);
                Boolean result = this.lawDataCollection(lawUpdatDate);

                if(result) entity.setStatus(StatusConstant.SUCCESS.name());
                schedulerUpdateDateRepository.save(entity);
            } else {
                if(entity.getStatus().equals(StatusConstant.PROCEEDING.name())){
                    Boolean result = this.lawDataCollection(lawUpdatDate);

                    if(result) entity.setStatus(StatusConstant.SUCCESS.name());
                    schedulerUpdateDateRepository.save(entity);
                } else {
                    // TODO:: 현 STATUS가 PROCEEDING가 아닐 경우도 [SUCCESS 여도..] 데이터 조회를 하도록 변경, 매일 데이터 현행화를 하기 위함
                    entity.setStatus(StatusConstant.PROCEEDING.name());

                    Boolean result = this.lawDataCollection(lawUpdatDate);
                    if(result) entity.setStatus(StatusConstant.SUCCESS.name());
                    schedulerUpdateDateRepository.save(entity);
                }
            }
        } else {
            SchedulerUpdateDateEntity entity = SchedulerUpdateDateEntity.builder()
                .name(SchedulerConstant.LAW_DATA_PORTAL_UPDATE_DATE.name())
                .updateDt(LocalDate.parse(lawUpdatDate))
                .status(StatusConstant.PROCEEDING.name())
                .build();

            schedulerUpdateDateRepository.save(entity);
            Boolean result = this.lawDataCollection(lawUpdatDate);

            if(result) entity.setStatus(StatusConstant.SUCCESS.name());
            schedulerUpdateDateRepository.save(entity);
        }

    }

    /**
     * 법률 데이터 Select
     * @throws Exception
     */
    private Boolean lawDataCollection(String lastUpdateDt) throws Exception{

        Integer[] categoryArray = {
            1, 2, 3, 4, 5, 
            // 6, TODO :: 추후 다시 생각, 일단 제외
            // 7, TODO :: 추후 다시 생각, 일단 제외
        };

        for(Integer categoryNum : categoryArray){
            
            Integer count = 1;
            Integer retryCount = 3;

            while(true){
                try {
                    ResponseEntity<LawRS> initDataWrapper = potalComponent.findLawDataByQuery(DATA_LAW_KEY, count, DATA_SIZE, " ", categoryNum);

                    LawRS initData = initDataWrapper.getBody();

                    if(!initData.getResponse().getHeader().getResultCode().equals("00")) {
                        if(retryCount <= 0) return false;

                        retryCount -= 1;
                        continue;
                    }

                    // Items 데이터가 없는경우
                    if(initData.getResponse().getBody().getItems() == null){
                        break;
                    }

                    List<Item> items = initData.getResponse().getBody().getItems().getItem();

                    if(items.size() < DATA_SIZE){
                        this.save(items, lastUpdateDt);
                        break;
                    } else{
                        this.save(items, lastUpdateDt);
                    }
                    retryCount = 3;
                    ++count;
                } catch (Exception e) {
                    log.error("Exception Class", e.getClass());
                    log.error("", e);

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * DB INSERT 메소드
     * @param items API통신으로 가져온 데이터
     * @return
     */
    @Transactional
    private Boolean save(List<Item> items, String lastUpdateDt){

        Set<String> docIdSet = items.stream().map(Item::getDoc_id).collect(Collectors.toSet());
        List<String> docIdList = new ArrayList<>(docIdSet);

        List<LawEntity> lawEntities = lawRepository.findByDocIdIn(docIdList);
        Map<String, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getDoc_id, item -> item));

        List<LawEntity> lawEntityList = new ArrayList<>();

        for (LawEntity lawEntity : lawEntities) {
            docIdSet.removeIf(key -> key.equals(lawEntity.getDocId()));
            
            Item item = itemMap.get(lawEntity.getDocId());

            if(item != null){

                // 데이터가 변경됨
                if(!item.getHighlight_content().equals(lawEntity.getHighlightContent())){
                    LawEntity addEntity = item.toEntity();
                    addEntity.setLawIdx(lawEntity.getLawIdx());
                    addEntity.setContent(addEntity.getHighlightContent().replace(" ", ""));
                    addEntity.setLastUpdateDt(LocalDate.parse(lastUpdateDt));
                    addEntity.setCreateDt(LocalDate.now());
                    addEntity.setView(0L);
 
                    lawEntityList.add(addEntity);

                    LawUpdateHistoryEntity lawUpdateHistoryEntity = LawUpdateHistoryEntity.builder()
                        .lawEntity(addEntity)
                        .oldContent(lawEntity.getHighlightContent())
                        .newContent(item.getHighlight_content())
                        .createDt(LocalDateTime.now())
                        .build();

                    lawUpdateHistoryRepository.save(lawUpdateHistoryEntity);
                }
            }
        }

        // DB에 없는 INSERT할 데이터
        for(String docId : docIdSet){
            LawEntity addEntity = itemMap.get(docId).toEntity();
            addEntity.setContent(addEntity.getHighlightContent().replace(" ", ""));
            addEntity.setLastUpdateDt(LocalDate.parse(lastUpdateDt));

            lawEntityList.add(addEntity);
        }

        lawRepository.saveAll(lawEntityList);

        return true;
    }

    private String getLawUpdatDate()  throws Exception{
        String crollingURL = "https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15123696#/API목록/smartSearch";
        Document doc = Jsoup.connect(crollingURL).get();

        Elements dataTableTR = doc.select("table tr");
        Elements dataElements = dataTableTR.select(".bg-skyblue");

        String lawUpdatDate = "";

        for (Element row : dataElements) {
            Elements thElements = row.select("th");
            Elements tdElements = row.select("td");

            String title = thElements.text();

            if(title.equals("수정일")){
                lawUpdatDate = tdElements.text();
            }
        }

        return lawUpdatDate;
    }

}