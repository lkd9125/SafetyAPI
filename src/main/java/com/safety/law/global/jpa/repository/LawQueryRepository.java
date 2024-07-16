package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.law.model.search.SearchLawCountRS.LawCategoryCount;
import com.safety.law.domain.law.model.search.SearchLawModel;
import com.safety.law.domain.law.model.search.SearchLawRQ;
import com.safety.law.global.jpa.entity.LawEntity;
import com.safety.law.global.jpa.entity.QLawEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LawQueryRepository {

    private final JPAQueryFactory query;

    public List<SearchLawModel> findKeyWordByHilightContentOrTitleAndCategory(SearchLawRQ rq){

        Integer offset = (rq.getPageNum() - 1) * rq.getRow();
        Integer limit = rq.getRow();

        String keyword = "%" + rq.getKeyWord() + "%";
        String replaceKeyword = "%" + keyword.replace(" ", "") + "%";

        QLawEntity qLawEntity = QLawEntity.lawEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.or(Expressions.stringTemplate("replace({0}, ' ', '')", qLawEntity.title).like(replaceKeyword));
        builder.or(qLawEntity.content.like(replaceKeyword));

        if(rq.getCategory() != null && rq.getCategory() > 0){
            builder.and(qLawEntity.category.eq(String.valueOf(rq.getCategory())));
        }

        NumberExpression<Integer> titleWeight = Expressions
            .cases()
            .when(qLawEntity.title.like(keyword))
                .then(2)
            .when(Expressions.stringTemplate("replace({0}, ' ', '')", qLawEntity.title).like(replaceKeyword))
                .then(1)
            .otherwise(0);

        return query
            .select(Projections.bean(
                SearchLawModel.class,
                qLawEntity.lawIdx.as("lawIdx"),
                qLawEntity.title.as("title"),
                qLawEntity.docId.as("lawDocId"),
                qLawEntity.category.as("category")
            ))
            .from(qLawEntity)
            .where(builder)
            .orderBy(titleWeight.desc())
            .offset(offset)
            .limit(limit)
            .fetch();
    }

    public List<LawCategoryCount> countByKeyword(String keyword){
        String queryKeyword = "%" + keyword.replace(" ", "") + "%";

        QLawEntity qLawEntity = QLawEntity.lawEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.or(Expressions.stringTemplate("replace({0}, ' ', '')", qLawEntity.title).like(queryKeyword));
        builder.or(qLawEntity.content.like(queryKeyword));

        return query
            .select(Projections.bean(
                LawCategoryCount.class,
                qLawEntity.category.as("category"),
                qLawEntity.category.count().as("count")
            ))
            .from(qLawEntity)
            .where(builder)
            .groupBy(qLawEntity.category)
            .fetch();
    }

}
    