package com.safety.law.domain.law.model.search;

import java.util.List;

import com.safety.law.domain.law.constant.CategoryConstant;

import lombok.Data;

@Data
public class SearchLawCountRS {

    private List<LawCategoryCount> categoryCounts;


    @Data
    public static class LawCategoryCount{
        
        private String category;

        private String categoryDesc;

        private Long count;

        public void setCategory(String category){
            this.category = category;
            this.categoryDesc = CategoryConstant.findDescByNumber(Integer.parseInt(category));
        }
    }

}
