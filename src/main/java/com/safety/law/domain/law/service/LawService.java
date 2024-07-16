package com.safety.law.domain.law.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.IOException;

import org.apache.logging.log4j.util.PropertySource.Comparator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.domain.law.constant.CategoryConstant;
import com.safety.law.domain.law.model.comment.CreateCommentRQ;
import com.safety.law.domain.law.model.detail.CommentModel;
import com.safety.law.domain.law.model.detail.DetailLawRS;
import com.safety.law.domain.law.model.detail.DetailLawRedisModel;
import com.safety.law.domain.law.model.detail.DetailLawRedisModel.LawExpiredModel;
import com.safety.law.domain.law.model.history.LawHistoryRQ;
import com.safety.law.domain.law.model.history.LawHistoryRS;
import com.safety.law.domain.law.model.history.LawHistoryRS.LawHistoryInfomation;
import com.safety.law.domain.law.model.ranking.KeywordModel;
import com.safety.law.domain.law.model.ranking.RankingViewRS;
import com.safety.law.domain.law.model.ranking.RankingViewRS.ScoreModel;
import com.safety.law.domain.law.model.search.EnhanceSearchLawRQ;
import com.safety.law.domain.law.model.search.EnhanceSearchLawRS;
import com.safety.law.domain.law.model.search.SearchChangeLawRQ;
import com.safety.law.domain.law.model.search.SearchChangeLawRS;
import com.safety.law.domain.law.model.search.SearchLawCountRQ;
import com.safety.law.domain.law.model.search.SearchLawCountRS;
import com.safety.law.domain.law.model.search.SearchLawModel;
import com.safety.law.domain.law.model.search.SearchLawRQ;
import com.safety.law.domain.law.model.search.SearchRedisModel;
import com.safety.law.domain.law.model.search.SearchChangeLawRS.LawChangeInfomation;
import com.safety.law.domain.law.model.search.SearchLawCountRS.LawCategoryCount;
import com.safety.law.domain.law.model.search.SearchRedisModel.LawKeywordModel;
import com.safety.law.domain.scheduler.model.LawRS;
import com.safety.law.domain.scheduler.model.LawRS.Item;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.external.PublicDataPotalComponent;
import com.safety.law.global.jpa.entity.CommentEntity;
import com.safety.law.global.jpa.entity.DayRankEntity;
import com.safety.law.global.jpa.entity.LawEntity;
import com.safety.law.global.jpa.entity.LawUpdateHistoryEntity;
import com.safety.law.global.jpa.entity.UserLawReadHistoryEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.DayRankQueryRepository;
import com.safety.law.global.jpa.repository.DayRankRepository;
import com.safety.law.global.jpa.repository.LawQueryRepository;
import com.safety.law.global.jpa.repository.LawRepository;
import com.safety.law.global.jpa.repository.LawUpdateHistoryQueryRepository;
import com.safety.law.global.jpa.repository.UserLawReadHistoryQueryRepository;
import com.safety.law.global.jpa.repository.UserLawReadHistoryRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.Principal;
import com.safety.law.global.util.CookieUtil;
import com.safety.law.global.util.DateUtil;
import com.safety.law.global.util.HttpUtil;
import com.safety.law.global.util.JsonUtils;
import com.safety.law.global.util.RedisUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LawService {

    private final UsersRepository usersRepository;

    private final LawRepository lawRepository;

    private final LawQueryRepository lawQueryRepository;

    private final LawUpdateHistoryQueryRepository lawUpdateHistoryQueryRepository;
    
    private final UserLawReadHistoryRepository userLawReadHistoryRepository;
    
    private final UserLawReadHistoryQueryRepository userLawReadHistoryQueryRepository;

    private final DayRankRepository dayRankRepository;

    private final DayRankQueryRepository dayRankQueryRepository;

    private final PublicDataPotalComponent publicDataPotalComponent;

    private final String VIEW_COOKIE_NAME = "LAW_VISIT";

    private final Integer EXPIRED_TIME = 60 * 60 * 24;

    private final RedisUtil redisUtil;

    private final HttpServletRequest request;

    @Value("${data.portal.law-key}")
    private String DATA_LAW_KEY;

    /**
     * 법률 조회, Title이나 content에 키워드가 들어가 있으면 조회함.
     * @param rq
     * @return
     */
    @Transactional
    public List<SearchLawModel> searchLaw(SearchLawRQ rq) {

        List<SearchLawModel> result = lawQueryRepository.findKeyWordByHilightContentOrTitleAndCategory(rq);

        Boolean keywordNull = rq.getKeyWord() !=null; // Keyword NULL체크
        Boolean keywordEmpty = !rq.getKeyWord().replace(" ", "").isEmpty();
        Boolean keywordDuplication = this.lawKeyowrdValid(rq.getKeyWord());

        Boolean valid = keywordNull && keywordEmpty && keywordDuplication;
        
        if(valid){
            DayRankEntity dayRankEntity = DayRankEntity.builder()
            .keyword(rq.getKeyWord())
            .build();
            
            dayRankRepository.save(dayRankEntity);
        }

        return result;
    }

    /**
     * 카테고리별 검색어에 해당하는 데이터 건수 조회
     * @param rq
     * @return
     */
    public SearchLawCountRS searchLawCount(@Valid SearchLawCountRQ rq) {
    
        List<LawCategoryCount> categoryCounts = lawQueryRepository.countByKeyword(rq.getKeyword());

        SearchLawCountRS result = new SearchLawCountRS();
        result.setCategoryCounts(categoryCounts);

        return result;
    }

    /**
     * 법률 상세조회
     * @param idx
     * @return
     */
    @Transactional
    public DetailLawRS detailLaw(Long idx) {

        LawEntity entity = lawRepository.findById(idx)
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        List<CommentModel> commentList = entity.getCommentList().stream().map(CommentModel::toModel).toList();

        Boolean viewDupliValid = this.lawViewValid(idx);

        if(viewDupliValid) entity.setView(entity.getView() + 1);

        String username = Principal.getUser();

        UserLawReadHistoryEntity userLawReadHistoryEntity = UserLawReadHistoryEntity.builder()
            .username(username)
            .lawIdx(idx)
            .build();

        
        userLawReadHistoryRepository.save(userLawReadHistoryEntity);

        DetailLawRS result =  DetailLawRS.toModel(entity);
        result.setCommentList(commentList);

        return result;
    }

    /**
     * 법률 댓글추가
     * @param rq
     * @return
     */
    @Transactional
    public Boolean createComment(@Valid CreateCommentRQ rq) {

        LawEntity lawEntity = lawRepository.findById(rq.getLawIdx())
                            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
                .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        CommentEntity comment = CommentEntity.builder()
            .content(rq.getContent())
            .lawEntity(lawEntity)
            .createUser(username)
            .updateUser(username)
            .nickname(usersEntity.getUsersDtl().getNickname())
            .build();

        List<CommentEntity> commentEntities = lawEntity.getCommentList() != null ? lawEntity.getCommentList() : new ArrayList<>();
        commentEntities.add(comment);

        lawEntity.setCommentList(commentEntities);

        lawRepository.save(lawEntity);

        return true;
    }

    /**
     * 조회수 증가 판단 [TRUE : 조회수 증가, FALSE : 조회수 증가하지 않음]
     * @param idx 조회하려는 Law의 idx
     * @return
     */
    private Boolean lawViewValid(Long idx){

        String username = Principal.getUser();
        String expierdTime = DateUtil.localDateTimeToString(LocalDateTime.now().plusDays(1));

        String value = redisUtil.getData(username);

        // 가장처음 상세조회 View조회 키가 없을경우
        if(value == null){
            LawExpiredModel lawExpiredModel = new LawExpiredModel();
            lawExpiredModel.setLawIdx(idx);
            lawExpiredModel.setExpiredDate(expierdTime);

            DetailLawRedisModel redisModel = new DetailLawRedisModel();            
            redisModel.setViewKey(Arrays.asList(lawExpiredModel));

            redisUtil.setData(username, redisModel, this.EXPIRED_TIME);

            return true;
        } else {
            DetailLawRedisModel detailLawRedisModel = JsonUtils.stringToObject(value, DetailLawRedisModel.class);
            Boolean result = !detailLawRedisModel.getViewKey().stream().anyMatch(model -> model.getLawIdx().equals(idx));

            // 키는 있으나 법률 IDX 값이 없는경우
            if(result){
                LawExpiredModel lawExpiredModel = new LawExpiredModel();
                lawExpiredModel.setLawIdx(idx);
                lawExpiredModel.setExpiredDate(expierdTime);

                detailLawRedisModel.getViewKey().add(lawExpiredModel);

                redisUtil.setData(username, detailLawRedisModel, this.EXPIRED_TIME);
            } else {
                List<LawExpiredModel> viewKey = detailLawRedisModel.getViewKey();

                Optional<LawExpiredModel> expiredModelWrapper = viewKey.stream()
                    .filter(expiredIdx -> expiredIdx.getLawIdx().longValue() == idx.longValue())
                    .findFirst();

                LawExpiredModel expiredModel = expiredModelWrapper.get();

                LocalDateTime expiredDate = DateUtil.stringToLocalDateTime(expiredModel.getExpiredDate(),"yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                
                Boolean expriedValid = expiredDate.isAfter(now);

                // 현재 시간보다 expried 시간이 지난 경우, 다시 조회수 1 상승 가능함
                if(!expriedValid){
                    viewKey.removeIf(key -> key.getLawIdx().longValue() == idx.longValue());

                    LawExpiredModel lawExpiredModel = new LawExpiredModel();
                    lawExpiredModel.setLawIdx(idx);
                    lawExpiredModel.setExpiredDate(expierdTime);

                    viewKey.add(lawExpiredModel);

                    detailLawRedisModel.setViewKey(viewKey);

                    redisUtil.setData(username, detailLawRedisModel, this.EXPIRED_TIME);

                    result = true;
                }
            }
            
            return result;
        }
    }

    /**
     * 조회수 증가 판단 [TRUE : 조회수 증가, FALSE : 조회수 증가하지 않음]
     * @param idx 조회하려는 Law의 idx
     * @return
     */
    @Deprecated
    private Boolean lawCookieViewValid(HttpServletRequest request, Long idx){

        Cookie cookie = CookieUtil.getCookie(request, this.VIEW_COOKIE_NAME);

        // 쿠키 자체가 없으면 쿠키를 만들고 VIEW 조회 수 증가
        if(cookie == null) {
            String cookieValue = new StringBuilder()
            .append(this.VIEW_COOKIE_NAME)
            .append(":")
            .append(DateUtil.nowToString())
            .toString();

            Cookie newCookie = new Cookie(VIEW_COOKIE_NAME, VIEW_COOKIE_NAME);
            newCookie.setMaxAge(0);

            return true;
        }

        Map<Long, LocalDateTime> lawViewDuplication = new HashMap<>();

        String[] lawEntry = cookie.getValue().split(",");

        for(String law : lawEntry){
            String[] entry = law.split(":");

            Long lawIdx = Long.parseLong(entry[0]);
            LocalDateTime viewLimitTime = DateUtil.stringToLocalDateTime(entry[1]);

            lawViewDuplication.put(lawIdx, viewLimitTime);
        }


        return false;
    }

    /**
     * 최근 조회한 법령 조회
     * @param rq
     * @return
     */
    public LawHistoryRS getLawHistory(@Valid LawHistoryRQ rq) {

        String username = Principal.getUser();

        List<Long> lawIdxList = userLawReadHistoryQueryRepository.findTop5CreateDtDescLimit5(rq.getPageNum(), rq.getRow(), username);
        Long count = userLawReadHistoryRepository.countByUsername(username).orElseThrow(() -> new AppException(ExceptionCode.DB_ERROR));

        List<LawEntity> lawEntities = lawRepository.findAllById(lawIdxList);

        List<LawHistoryInfomation> lawHistory = lawEntities.stream()
            .map(entity -> {
                LawHistoryInfomation info = new LawHistoryInfomation();
                info.setLawIdx(entity.getLawIdx());
                info.setCategory(CategoryConstant.findDescByNumber(Integer.parseInt(entity.getCategory())));
                info.setTitle(entity.getTitle());
                return info;
            })
            .toList();


        LawHistoryRS result = new LawHistoryRS();
        result.setCount(count);
        result.setPageNum(rq.getPageNum());
        result.setLawHistory(lawHistory);

        return result;
    }

    /**
     * 검색어 랭킹
     * @param rq
     * @return
     */
    public RankingViewRS rankingView() {

        String[] rankingScoreTag = {"ONE_HOURS", "TODAY", "SEVEN_DAY"};
        Map<String, List<KeywordModel>> rankingData = dayRankQueryRepository.getRankingList(rankingScoreTag);

        Map<String, Double> keywordScoreMap = new HashMap<>();
        
        for(Map.Entry<String, List<KeywordModel>> entry : rankingData.entrySet()){
            String key = entry.getKey();

            for(KeywordModel model : entry.getValue()){
                String keyword = model.getKeyword();
                Double score = getRankingScore(key, model.getCount());

                Double oldScore = keywordScoreMap.get(keyword);

                keywordScoreMap.put(keyword, score + (oldScore == null ? 0 : oldScore));
            }
        }

        List<ScoreModel> scoreModels = new ArrayList<>();

        for(Map.Entry<String, Double> entry : keywordScoreMap.entrySet()){
            String keyword = entry.getKey();
            Double score = entry.getValue();

            ScoreModel node = new ScoreModel();
            node.setKeyword(keyword);
            node.setScore(score);

            scoreModels.add(node);
        }

        scoreModels.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        if (scoreModels.size() > 5) {
            scoreModels = scoreModels.subList(0, 5);
        }   

        RankingViewRS result = new RankingViewRS();
        result.setScoreList(scoreModels);

        return result;
    }

    private Double getRankingScore(String tag, Long count){

        Double result = 0.0;

        switch (tag) {
            case "ONE_HOURS":
                result = count * 0.15;
                break;
            case "TODAY":
                result = count * 0.12;
                break;
            case "SEVEN_DAY":
                result = count * 0.1;
                break;
        
            default:
                throw new AppException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
    
    private Boolean lawKeyowrdValid(String keyword){

        String key = "KEYWORD_" + HttpUtil.getClientIpAddr(request);
        String expierdTime = DateUtil.localDateTimeToString(LocalDateTime.now().plusMinutes(10));

        String value = redisUtil.getData(key);

        // 가장처음 상세조회 IP 조회 키가 없을경우
        if(value == null){
            LawKeywordModel lawKeywordModel = new LawKeywordModel();
            lawKeywordModel.setKeyword(keyword);
            lawKeywordModel.setExpiredDate(expierdTime);

            SearchRedisModel redisModel = new SearchRedisModel();
            redisModel.setKeywordKey(Arrays.asList(lawKeywordModel));

            redisUtil.setData(key, redisModel, this.EXPIRED_TIME);

            return true;
        } else {
            SearchRedisModel searchRedisModel = JsonUtils.stringToObject(value, SearchRedisModel.class);
            Boolean result = !searchRedisModel.getKeywordKey().stream().anyMatch(model -> model.getKeyword().equals(keyword));

            // 키는 있으나 Keyword 값이 없는경우
            if(result){
                LawKeywordModel lawKeywordModel = new LawKeywordModel();
                lawKeywordModel.setKeyword(keyword);
                lawKeywordModel.setExpiredDate(expierdTime);

                searchRedisModel.getKeywordKey().add(lawKeywordModel);

                redisUtil.setData(key, searchRedisModel, this.EXPIRED_TIME);
            } else {
                List<LawKeywordModel> keywordKey = searchRedisModel.getKeywordKey();

                Optional<LawKeywordModel> expiredModelWrapper = keywordKey.stream()
                    .filter(expiredIdx -> expiredIdx.getKeyword().equals(keyword))
                    .findFirst();

                LawKeywordModel expiredModel = expiredModelWrapper.get();

                LocalDateTime expiredDate = DateUtil.stringToLocalDateTime(expiredModel.getExpiredDate(),"yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                
                Boolean expriedValid = expiredDate.isAfter(now);

                // 현재 시간보다 expried 시간이 지난 경우, 다시 조회수 1 상승 가능함
                if(!expriedValid){
                    keywordKey.removeIf(target -> target.getKeyword().equals(keyword));

                    LawKeywordModel LawKeywordModel = new LawKeywordModel();
                    LawKeywordModel.setKeyword(keyword);
                    LawKeywordModel.setExpiredDate(expierdTime);

                    keywordKey.add(LawKeywordModel);

                    searchRedisModel.setKeywordKey(keywordKey);

                    redisUtil.setData(key, searchRedisModel, this.EXPIRED_TIME);

                    result = true;
                }
            }

            return result;
        }
    }

    /**
     * 향상된 법률검색, 없는 데이터를 API를 호출하며 조회함
     * @param rq
     * @return
     * @throws Exception
     */
    @Transactional
    public EnhanceSearchLawRS enhanceSearch(@Valid EnhanceSearchLawRQ rq) throws Exception{

        // 데이터 포털에서 법률 검색

        List<Item> items = new ArrayList<>();

        try {
            ResponseEntity<LawRS> initDataWrapper = publicDataPotalComponent.findLawDataByQuery(DATA_LAW_KEY, rq.getPageNum(), rq.getRow(), rq.getKeyWord(), rq.getCategory());
            LawRS initData = initDataWrapper.getBody();
            items = initData.getResponse().getBody().getItems().getItem();
        } catch (Exception e) {
            ResponseEntity<String> data = publicDataPotalComponent.findLawDataErrorByQuery(DATA_LAW_KEY, rq.getPageNum(), rq.getRow(), rq.getKeyWord(), rq.getCategory());
            log.warn("errorType : {}", e.getClass());
            log.warn("errorData => {}", data.getBody());
            throw new AppException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
        
        // 조회한 법률 데이터에서 DOC_ID만 추출
        List<String> createDocIdList = new ArrayList<>(items.stream().map(Item::getDoc_id).toList());

        // DB에서 조회한 법률 DOC_ID를 검색, 없는 데이터는 INSERT를 위함
        List<LawEntity> lawOldEntities = lawRepository.findByDocIdIn(createDocIdList);

        // DB 데이터에서 DOC_ID만 추출
        List<String> lawDocidEntities = lawOldEntities.stream().map(LawEntity::getDocId).toList();

        // 겹치는 DOC_ID를 삭제, DB에 INSERT할 데이터를 남기는 것
        createDocIdList.removeAll(lawDocidEntities);

        String lawLastDate = this.getLawUpdatDate();

        List<SearchLawModel> createLawModels = new ArrayList<>();

        // 만약 추가할 데이터가 있다면 Entity를 만들며 반환 객체로 변환함
        if(createDocIdList.size() > 0){
            List<LawEntity> lawEntities = items.stream().filter(item -> {
                for(String docId: createDocIdList){
                    if(item.getDoc_id().equals(docId)) return true;
                }
                return false;
            })
            .map(item -> {
                LawEntity entity = item.toEntity();
                entity.setLastUpdateDt(LocalDate.parse(lawLastDate));
                return entity;
            })
            .toList();
    
            lawEntities = lawRepository.saveAll(lawEntities);

            createLawModels = lawEntities.stream()
                .map(entity -> {
                    SearchLawModel model = new SearchLawModel();
                    model.setLawIdx(entity.getLawIdx());
                    model.setTitle(entity.getTitle());
                    model.setLawDocId(entity.getDocId());
                    model.setCategory(entity.getCategory());

                    return model;
                })
                .toList();
        }

        List<SearchLawModel> searchLawModels = new ArrayList<>();

        searchLawModels.addAll(createLawModels);
        searchLawModels.addAll(lawOldEntities.stream()
            .map(entity -> {
                SearchLawModel model = new SearchLawModel();
                model.setLawIdx(entity.getLawIdx());
                model.setTitle(entity.getTitle());
                model.setLawDocId(entity.getDocId());
                model.setCategory(entity.getCategory());

                return model;
            })
            .toList()
        );

        EnhanceSearchLawRS result = new EnhanceSearchLawRS();
        result.setSearchDataList(searchLawModels);

        return result;
    }

    /**
     * 웹사이트에서 마지막 수정일자 데이터 조회
     * @return
     * @throws Exception
     */
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

    @Transactional
    public SearchChangeLawRS changeLawList(@Valid SearchChangeLawRQ rq) {

        try {
            if(rq.getStartDate().isAfter(rq.getEndDate())) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
        } catch (NullPointerException e) {
            log.warn("START OR END DATE PARAMETER NULL");
        }

        List<LawUpdateHistoryEntity> lawUpdateList = lawUpdateHistoryQueryRepository.findByStartDateAndEndDate(rq);

        List<LawChangeInfomation> changeLawList = lawUpdateList.stream()
            .map(entity -> {

                LawEntity lawEntity = entity.getLawEntity();

                LawChangeInfomation info = new LawChangeInfomation();
                info.setLawIdx(lawEntity.getLawIdx());
                info.setCategory(CategoryConstant.findDescByNumber(Integer.parseInt(lawEntity.getCategory())));
                info.setTitle(lawEntity.getTitle());
                info.setChangeDate(entity.getCreateDt().toLocalDate());
                return info;
            })
            .toList();
        
        SearchChangeLawRS result = new SearchChangeLawRS();
        result.setPageNum(rq.getPageNum());
        result.setChangeLawList(changeLawList);

        return result;
    }

}