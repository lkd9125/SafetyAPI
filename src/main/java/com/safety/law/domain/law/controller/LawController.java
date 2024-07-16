package com.safety.law.domain.law.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.domain.law.model.category.SearchCategoryRS;
import com.safety.law.domain.law.model.comment.CreateCommentRQ;
import com.safety.law.domain.law.model.detail.DetailLawRS;
import com.safety.law.domain.law.model.history.LawHistoryRQ;
import com.safety.law.domain.law.model.history.LawHistoryRS;
import com.safety.law.domain.law.model.ranking.RankingViewRS;
import com.safety.law.domain.law.model.search.EnhanceSearchLawRQ;
import com.safety.law.domain.law.model.search.EnhanceSearchLawRS;
import com.safety.law.domain.law.model.search.SearchChangeLawRQ;
import com.safety.law.domain.law.model.search.SearchChangeLawRS;
import com.safety.law.domain.law.model.search.SearchLawCountRQ;
import com.safety.law.domain.law.model.search.SearchLawCountRS;
import com.safety.law.domain.law.model.search.SearchLawModel;
import com.safety.law.domain.law.model.search.SearchLawRQ;
import com.safety.law.domain.law.model.search.SearchLawRS;
import com.safety.law.domain.law.service.LawService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/law")
@RequiredArgsConstructor
public class LawController {

    private final LawService lawService;

    /**
     * 법률 카테고리 조회
     * @return
     */
    @GetMapping("/category")
    public ResponseEntity<SearchCategoryRS> searchCategory(){

        return ResponseEntity.ok().body(new SearchCategoryRS());
    }

    /**
     * 법률 조회 
     * @param rq keyWord: 검색키워드, category: 카테고리, pageNum: 페이지, row: 불러올 데이터크기
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<SearchLawRS> searchLaw(@Valid SearchLawRQ rq){
        List<SearchLawModel> searchData = lawService.searchLaw(rq);

        SearchLawRS result = new SearchLawRS();
        result.setSearchDataList(searchData);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 카테고리별 검색어에 해당하는 데이터 건수 조회
     * @param rq keyword: 검색어
     * @return
     */
    @GetMapping("/search/count")
    public ResponseEntity<SearchLawCountRS> searchLawCount(@Valid SearchLawCountRQ rq){

        SearchLawCountRS result = lawService.searchLawCount(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 법률 상세조회
     * @param idx 법률 고유번호
     * @return
     */
    @GetMapping("/detail/{idx}")
    public ResponseEntity<DetailLawRS> detailLaw(@PathVariable(name = "idx") Long idx){

        DetailLawRS result = lawService.detailLaw(idx);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 법률에 댓글 추가
     * @param rq lawIdx: 벌률 고유키, commont: 댓글 내용
     * @return
     */
    @PostMapping("/comment")
    public ResponseEntity<Boolean> createComment(@RequestBody @Valid CreateCommentRQ rq){

        Boolean result = lawService.createComment(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 법률 최근 조회내역 조회
     * @param rq pageNum: 페이지, row: 조회할 데이터 갯수
     * @return
     */
    @GetMapping("/history")
    public ResponseEntity<LawHistoryRS> getLawHistory(@Valid LawHistoryRQ rq){

        LawHistoryRS result = lawService.getLawHistory(rq);

        return ResponseEntity.ok().body(result);
    }

    
    /**
     * 검색어 랭킹
     * @param rq 검색어 랭킹 최대 숫자, 검색하고 싶은 날짜
     * @return
     */
    @GetMapping("/ranking/keyword")
    public ResponseEntity<RankingViewRS> rankingView(){

        RankingViewRS result = lawService.rankingView();
        
        return ResponseEntity.ok().body(result);
    }

    /**
     * 향상된 법률검색, 공공데이터포탈 API호출
     * @param rq
     * @return
     */
    @PostMapping("/enhance/search")
    public ResponseEntity<EnhanceSearchLawRS> enhanceSearch(@RequestBody @Valid EnhanceSearchLawRQ rq) throws Exception{

        EnhanceSearchLawRS result = lawService.enhanceSearch(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 최근 변경된 법률 데이터 조회, 날짜범위
     * @param rq
     * @return
     */
    @GetMapping("/change-list")
    public ResponseEntity<SearchChangeLawRS> changeLawList(@Valid SearchChangeLawRQ rq){

        SearchChangeLawRS result = lawService.changeLawList(rq);

        return ResponseEntity.ok().body(result);
    }

}
