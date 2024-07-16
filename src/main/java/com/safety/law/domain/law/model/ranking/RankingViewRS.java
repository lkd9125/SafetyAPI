package com.safety.law.domain.law.model.ranking;

import java.util.List;

import lombok.Data;

@Data
public class RankingViewRS {

    private List<ScoreModel> scoreList;

    @Data
    public static class ScoreModel{

        private String keyword; 
        
        private Double score;

    }

}
