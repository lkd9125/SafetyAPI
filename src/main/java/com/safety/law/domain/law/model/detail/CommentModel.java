package com.safety.law.domain.law.model.detail;

import java.time.LocalDateTime;

import com.safety.law.global.jpa.entity.CommentEntity;

import lombok.Data;

@Data
public class CommentModel {

    private Long commentIdx;

    private String content;

    private String username;

    private String nickname;

    private LocalDateTime lastUpdateDt;


    public static CommentModel toModel(CommentEntity entity){
        CommentModel model = new CommentModel();
        model.setCommentIdx(entity.getCommentIdx());
        model.setContent(entity.getContent());
        model.setUsername(entity.getUpdateUser());
        model.setNickname(entity.getNickname());
        model.setLastUpdateDt(entity.getUpdateDt());

        return model;
    }
}
