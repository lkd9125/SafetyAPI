package com.safety.law.domain.user.model.mypage;

import java.util.List;

import lombok.Data;

@Data
public class SaveBoardRS {

    private List<BoardModel> boardList;
    
    @Data
    public static class BoardModel{

        private Long idx;

        private String title;

        private String content;

        private String createDt;

        private String updateDt;

    }

}
