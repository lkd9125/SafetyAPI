package com.safety.law.domain.board.model.board;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SearchBoardRS {


    private List<BoardModel> boardList;

    @Data
    public static class BoardModel{

        private Long boardIdx;

        private String title;

        private String content;

        private Long heartCount;

        private Long commentCount;

        @JsonIgnore
        private Long selfHeartCount;

        private Boolean selfHeartValid;

        private LocalDateTime createDt;

        private LocalDateTime updateDt;

        private String createUser;

        private String createUserName;

        private String createUserProfileUrl;

        public Boolean getSelfHeartValid(){
            return this.selfHeartCount > 0 ? true : false;
        }

        // private MainCommentModel mainCommentModel;


    }

    // @Data 
    // public static class MainCommentModel{

    //     private Long boardCommnetIdx;

    //     private String content;

    //     private LocalDateTime createDt;
        
    //     private String createUser;
        
    // }
}
