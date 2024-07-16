package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.global.jpa.entity.QUserLawReadHistoryEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserLawReadHistoryQueryRepository {

    private final JPAQueryFactory query;

    public List<Long> findTop5CreateDtDescLimit5(Integer pageNum, Integer row, String username){

        Integer offset = (pageNum - 1) * row;
        Integer limit = row;

        QUserLawReadHistoryEntity entity = QUserLawReadHistoryEntity.userLawReadHistoryEntity;
        
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(entity.username.eq(username));

        return query
            .select(entity.lawIdx)
            .from(entity)
            .where(builder)
            .groupBy(entity.lawIdx)
            .orderBy(entity.createDt.desc())
            .offset(offset)
            .limit(limit)
            .fetch();
    }

}
