package com.safety.law.domain.law.model.search;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class SearchChangeLawRS {

    @JsonInclude(value = Include.NON_NULL)
    private Long count; // 총 데이터 갯수

    private Integer pageNum;

    private List<LawChangeInfomation> changeLawList;

    @Data
    public static class LawChangeInfomation{

        private Long lawIdx;

        private String category;

        private String title;

        private LocalDate changeDate;
    }


}
