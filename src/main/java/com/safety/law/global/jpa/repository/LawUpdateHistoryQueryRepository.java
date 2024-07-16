package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.law.model.search.SearchChangeLawRQ;
import com.safety.law.global.jpa.entity.LawUpdateHistoryEntity;
import com.safety.law.global.jpa.entity.QLawUpdateHistoryEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class LawUpdateHistoryQueryRepository {

    private final JPAQueryFactory query;


    public List<LawUpdateHistoryEntity> findByStartDateAndEndDate(SearchChangeLawRQ rq){

        Integer offset = (rq.getPageNum() - 1) * rq.getRow();
        Integer limit = rq.getRow();

        QLawUpdateHistoryEntity qLawUpdateHistoryEntity = QLawUpdateHistoryEntity.lawUpdateHistoryEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if(rq.getStartDate() != null) {
            builder.and(qLawUpdateHistoryEntity.createDt.goe(rq.getStartDate().atStartOfDay()));
        }
        if(rq.getEndDate() != null) {
            builder.and(qLawUpdateHistoryEntity.createDt.loe(rq.getEndDate().atTime(23, 59, 59)));
        }

        return query
                .select(
                    qLawUpdateHistoryEntity
                )
                .from(qLawUpdateHistoryEntity)
                .where(builder)
                .orderBy(qLawUpdateHistoryEntity.createDt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }
}   

