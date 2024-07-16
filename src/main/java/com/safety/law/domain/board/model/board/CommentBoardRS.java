package com.safety.law.domain.board.model.board;

import java.util.List;

import lombok.Data;

@Data
public class CommentBoardRS {

    private List<CommentModel> commentList;

    @Data
    public static class CommentModel{

        private Long commentIdx; 

        private Long parentIdx;

        private String content; // 내용

        private String createDt; // 생성일

        private String updateDt; // 수성일

        private String createUser; // 생성자

        private String createUserName; // 유저 이름

        private String createUserProfileUrl; // 유저 프로필이미지
    }

}
