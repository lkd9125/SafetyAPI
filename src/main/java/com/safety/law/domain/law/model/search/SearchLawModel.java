package com.safety.law.domain.law.model.search;

import com.safety.law.domain.law.constant.CategoryConstant;
import com.safety.law.global.jpa.entity.LawEntity;

import lombok.Data;

@Data
public class SearchLawModel {

    private Long lawIdx;

    private String lawDocId;

    private String title;

    private String category;

    public SearchLawModel toModel(LawEntity entity){
        SearchLawModel model = new SearchLawModel();
        model.setLawIdx(entity.getLawIdx());
        model.setLawDocId(entity.getDocId());
        model.setTitle(entity.getTitle());
        model.setCategory(entity.getCategory());
        
        return model;
    }

    public void setCategory(String category){
        this.category = CategoryConstant.findDescByNumber(Integer.parseInt(category));
    }
}
