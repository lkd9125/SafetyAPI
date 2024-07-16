package com.safety.law.domain.law.model.detail;

import java.util.List;
import java.util.ArrayList;

import com.safety.law.domain.law.constant.CategoryConstant;
import com.safety.law.global.jpa.entity.LawEntity;

import lombok.Data;

@Data
public class DetailLawRS {

    private Long lawIdx;

    private String title;

    private String hilightContent;

    private String docId;

    private String category;

    private String categoryDesc;

    private Long view;

    private String lastUpdateDt; // 최종 수정 일

    private List<CommentModel> commentList = new ArrayList<>();

    public static DetailLawRS toModel(LawEntity entity){
        DetailLawRS model = new DetailLawRS();
        model.setLawIdx(entity.getLawIdx());
        model.setTitle(entity.getTitle());
        model.setHilightContent(entity.getHighlightContent());
        model.setDocId(entity.getDocId());
        model.setCategory(entity.getCategory());
        model.setCategoryDesc(CategoryConstant.findDescByNumber(Integer.parseInt(entity.getCategory())));
        model.setView(entity.getView());
        model.setLastUpdateDt(entity.getLastUpdateDt().toString());

        return model;
    }

}
