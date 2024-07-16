package com.safety.law.domain.law.model.history;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class LawHistoryRS {

    @JsonInclude(value = Include.NON_NULL)
    private Long count; // 총 데이터 갯수

    private Integer pageNum;

    private List<LawHistoryInfomation> lawHistory;


    @Data
    public static class LawHistoryInfomation{

        private Long lawIdx;

        private String category;

        private String title;
    }

}
