package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.board.model.board.SearchBoardRQ;
import com.safety.law.domain.board.model.board.SearchBoardRS.BoardModel;
import com.safety.law.global.jpa.entity.QBoardCommentEntity;
import com.safety.law.global.jpa.entity.QBoardEntity;
import com.safety.law.global.jpa.entity.QBoardHeartReferenceEntity;
import com.safety.law.global.jpa.entity.QUsersDtlEntity;
import com.safety.law.global.jpa.entity.QUsersEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BoardQueryRepository {

    private final JPAQueryFactory query;

    public List<BoardModel> getBoardList(SearchBoardRQ rq, String username){

        Integer offset = (rq.getPageNum() - 1) * rq.getRow();
        Integer limit = rq.getRow();

        String keyword = "%" + rq.getKeyWord() + "%";
        String replaceKeyword = "%" + keyword.replace(" ", "") + "%";

        QBoardEntity qBoardEntity = QBoardEntity.boardEntity;
        QBoardCommentEntity qBoardCommentEntity = QBoardCommentEntity.boardCommentEntity;
        QBoardHeartReferenceEntity qBoardHeartRefrenceEntity = QBoardHeartReferenceEntity.boardHeartReferenceEntity;
        QUsersDtlEntity qUsersDtlEntity = QUsersDtlEntity.usersDtlEntity;
        QUsersEntity qUsersEntity = QUsersEntity.usersEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.or(Expressions.stringTemplate("replace({0}, ' ', '')", qBoardEntity.title).like(replaceKeyword));

        BooleanBuilder subBuilder = new BooleanBuilder();
        subBuilder.and(qBoardHeartRefrenceEntity.usersEntity.username.eq(username));
        subBuilder.and(qBoardHeartRefrenceEntity.boardEntity.boardIdx.eq(qBoardEntity.boardIdx));

        NumberExpression<Integer> titleWeight = Expressions
            .cases()
            .when(qBoardEntity.title.like(keyword))
                .then(2)
            .when(Expressions.stringTemplate("replace({0}, ' ', '')", qBoardEntity.title).like(replaceKeyword))
                .then(1)
            .otherwise(0);

        return query
        .select(
            Projections.bean(BoardModel.class, 
                qBoardEntity.boardIdx.as("boardIdx"),
                qBoardEntity.title.as("title"),
                qBoardEntity.content.as("content"),
                qBoardHeartRefrenceEntity.countDistinct().as("heartCount"),
                qBoardCommentEntity.countDistinct().as("commentCount"),
                qBoardEntity.createUser.as("createUser"),
                qBoardEntity.createDt.as("createDt"),
                qBoardEntity.updateDt.as("updateDt"),
                qUsersDtlEntity.name.as("createUserName"),
                qUsersEntity.profileImgUrl.as("createUserProfileUrl"),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(qBoardHeartRefrenceEntity.count())
                        .from(qBoardHeartRefrenceEntity)
                        .where(subBuilder),
                    "selfHeartCount"
                )
            )
        )
        .from(qBoardEntity)
        .leftJoin(qBoardCommentEntity)
            .on(qBoardCommentEntity.boardEntity.boardIdx.eq(qBoardEntity.boardIdx))
        .leftJoin(qBoardHeartRefrenceEntity)
            .on(qBoardHeartRefrenceEntity.boardEntity.boardIdx.eq(qBoardEntity.boardIdx))
        .leftJoin(qUsersDtlEntity)
            .on(qBoardEntity.createUser.eq(qUsersDtlEntity.username))
        .leftJoin(qUsersEntity)
            .on(qBoardEntity.createUser.eq(qUsersEntity.username))
        .where(builder)
        .groupBy(
            qBoardEntity.boardIdx,
            qBoardEntity.title,
            qBoardEntity.content,
            qBoardEntity.createUser,
            qBoardEntity.createDt,
            qBoardEntity.updateDt
        )
        .orderBy(
            titleWeight.desc(),
            qBoardEntity.updateDt.desc()
        )
        .offset(offset)
        .limit(limit)
        .fetch()
        ;
    }

    public BoardModel getBoardDetail(Long idx, String username){


        QBoardEntity qBoardEntity = QBoardEntity.boardEntity;
        QBoardCommentEntity qBoardCommentEntity = QBoardCommentEntity.boardCommentEntity;
        QBoardHeartReferenceEntity qBoardHeartRefrenceEntity = QBoardHeartReferenceEntity.boardHeartReferenceEntity;
        QUsersEntity qUsersEntity = QUsersEntity.usersEntity;
        QUsersDtlEntity qUsersDtlEntity = QUsersDtlEntity.usersDtlEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBoardEntity.boardIdx.eq(idx));

        return query
        .select(
            Projections.bean(BoardModel.class, 
                qBoardEntity.boardIdx.as("boardIdx"),
                qBoardEntity.title.as("title"),
                qBoardEntity.content.as("content"),
                qBoardHeartRefrenceEntity.countDistinct().as("heartCount"),
                qBoardCommentEntity.countDistinct().as("commentCount"),
                qBoardEntity.createUser.as("createUser"),
                qBoardEntity.createDt.as("createDt"),
                qBoardEntity.updateDt.as("updateDt"),
                qUsersDtlEntity.name.as("createUserName"),
                qUsersEntity.profileImgUrl.as("createUserProfileUrl"),
                ExpressionUtils.as(
                    JPAExpressions
                        .select(qBoardHeartRefrenceEntity.count())
                        .from(qBoardHeartRefrenceEntity)
                        .where(qBoardHeartRefrenceEntity.usersEntity.username.eq(username)),
                    "selfHeartCount"
                )
            )
        )
        .from(qBoardEntity)
        .leftJoin(qBoardCommentEntity)
            .on(qBoardCommentEntity.boardEntity.boardIdx.eq(qBoardEntity.boardIdx))
        .leftJoin(qBoardHeartRefrenceEntity)
            .on(qBoardHeartRefrenceEntity.boardEntity.boardIdx.eq(qBoardEntity.boardIdx))
        .leftJoin(qUsersDtlEntity)
            .on(qBoardEntity.createUser.eq(qUsersDtlEntity.username))
        .leftJoin(qUsersEntity)
            .on(qBoardEntity.createUser.eq(qUsersEntity.username))
        .where(builder)
        .groupBy(
            qBoardEntity.boardIdx,
            qBoardEntity.title,
            qBoardEntity.content,
            qBoardEntity.createUser,
            qBoardEntity.createDt,
            qBoardEntity.updateDt
        )
        .fetchOne()
        ;
    }

}
