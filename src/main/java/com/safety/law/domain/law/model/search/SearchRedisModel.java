package com.safety.law.domain.law.model.search;

import java.util.List;

import lombok.Data;

@Data
public class SearchRedisModel {

    private List<LawKeywordModel> keywordKey;

    @Data
    public static class LawKeywordModel{

        private String keyword;

        private String expiredDate;

    }
}
