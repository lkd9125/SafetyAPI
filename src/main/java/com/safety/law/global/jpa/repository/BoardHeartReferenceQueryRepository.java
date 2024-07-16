package com.safety.law.global.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.user.model.mypage.SaveBoardRS.BoardModel;
import com.safety.law.global.jpa.entity.BoardHeartReferenceEntity;
import com.safety.law.global.jpa.entity.QBoardEntity;
import com.safety.law.global.jpa.entity.QBoardHeartReferenceEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BoardHeartReferenceQueryRepository {

    private final JPAQueryFactory query;

    public BoardHeartReferenceEntity boardSaveValid(String username, Long idx){

        QBoardHeartReferenceEntity qBoardHeartReferenceEntity = QBoardHeartReferenceEntity.boardHeartReferenceEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBoardHeartReferenceEntity.boardEntity.boardIdx.eq(idx));
        builder.and(qBoardHeartReferenceEntity.usersEntity.username.eq(username));

        return query
            .select(qBoardHeartReferenceEntity)
            .from(qBoardHeartReferenceEntity)
            .where(builder)
            .fetchOne();
    }

    public List<BoardModel> findByUsername(String username){

        QBoardHeartReferenceEntity qBoardHeartReferenceEntity = QBoardHeartReferenceEntity.boardHeartReferenceEntity;
        QBoardEntity qBoardEntity = QBoardEntity.boardEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBoardHeartReferenceEntity.usersEntity.username.eq(username));

        String dateFormat = "%Y-%m-%d %H:%i";

        return query
            .select(Projections.bean(
                    BoardModel.class,
                    qBoardEntity.boardIdx.as("idx"),
                    qBoardEntity.title.as("title"),
                    qBoardEntity.content.as("content"),
                    Expressions.stringTemplate("DATE_FORMAT({0}, {1})", qBoardEntity.createDt, dateFormat).as("createDt"),
                    Expressions.stringTemplate("DATE_FORMAT({0}, {1})", qBoardEntity.updateDt, dateFormat).as("updateDt")
                )
            )
            .from(qBoardHeartReferenceEntity)
            .where(builder)
            .leftJoin(qBoardEntity)
                .on(qBoardEntity.boardIdx.eq(qBoardHeartReferenceEntity.boardEntity.boardIdx))
            .orderBy(qBoardEntity.createDt.desc())
            .fetch();
    }

    

}
