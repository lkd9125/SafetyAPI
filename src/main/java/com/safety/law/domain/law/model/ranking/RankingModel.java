package com.safety.law.domain.law.model.ranking;

import java.util.List;

import com.safety.law.domain.law.model.ranking.RankingViewRS.ScoreModel;

import lombok.Data;

@Data
public class RankingModel {

    private String desc;

    private List<ScoreModel> scoreList;
}
