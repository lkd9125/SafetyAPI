package com.safety.law.global.jpa.repository;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.law.model.ranking.KeywordModel;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.QDayRankEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DayRankQueryRepository {

    private final JPAQueryFactory query;

    public Map<String, List<KeywordModel>> getRankingList(String[] rankingScoreTag){

        QDayRankEntity entity = QDayRankEntity.dayRankEntity;

        Map<String, List<KeywordModel>> result = new HashMap<String, List<KeywordModel>>();
        
        for(String tag : rankingScoreTag){
            
            BooleanBuilder builder = getRankingListBooleanBuilder(tag);

            result.put(tag, query  
                .select(Projections.bean(KeywordModel.class, 
                        entity.keyword,
                        entity.count().as("count")
                    )
                )
                .from(entity)
                .where(builder)
                .groupBy(entity.keyword)
                .orderBy(entity.count().desc())
                // .limit(5)
                .fetch()
            );
        }

        return result;
    }

    private BooleanBuilder getRankingListBooleanBuilder(String tag){
        QDayRankEntity entity = QDayRankEntity.dayRankEntity;

        BooleanBuilder builder = new BooleanBuilder();

        switch (tag) {
            case "ONE_HOURS":
                builder.and(entity.createDt.goe(LocalDateTime.now().minusHours(1)));
                break;
            case "TODAY":
                builder.and(
                    Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", entity.createDt).eq(
                    Expressions.stringTemplate("DATE_FORMAT(NOW(), '%Y-%m-%d')")
                ));
                break;
            case "SEVEN_DAY":
                builder.and(entity.createDt.goe(LocalDateTime.now().minusDays(7)));
                break;
        
            default:
                throw new AppException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }

        return builder;
    }
}
