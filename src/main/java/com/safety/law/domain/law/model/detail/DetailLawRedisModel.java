package com.safety.law.domain.law.model.detail;

import java.util.List;

import lombok.Data;

@Data
public class DetailLawRedisModel {

    private List<LawExpiredModel> viewKey;


    @Data
    public static class LawExpiredModel {

        private Long lawIdx;

        private String expiredDate;
        
    }



}
