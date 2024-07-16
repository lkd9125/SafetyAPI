package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.board.model.board.CommentBoardRS.CommentModel;
import com.safety.law.global.jpa.entity.QBoardCommentEntity;
import com.safety.law.global.jpa.entity.QUsersDtlEntity;
import com.safety.law.global.jpa.entity.QUsersEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BoardCommentQueryRepository {

    private final JPAQueryFactory query;

    public List<CommentModel> getComment(Long idx){
        QBoardCommentEntity qBoardCommentEntity = QBoardCommentEntity.boardCommentEntity;
        QUsersEntity qUsersEntity = QUsersEntity.usersEntity;
        QUsersDtlEntity qUsersDtlEntity = QUsersDtlEntity.usersDtlEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qBoardCommentEntity.boardEntity.boardIdx.eq(idx));

        Expressions.stringTemplate("replace({0}, ' ', '')", qBoardCommentEntity.createDt).as("createDt");
        Expressions.stringTemplate("replace({0}, ' ', '')", qBoardCommentEntity.updateDt).as("updateDt");
        String dateFormat = "%Y-%m-%d %H:%i";

        return query
            .select(Projections.bean(CommentModel.class,
                    qBoardCommentEntity.boardCommentIdx.as("commentIdx"),
                    qBoardCommentEntity.parentIdx.as("parentIdx"),
                    qBoardCommentEntity.content.as("content"),
                    Expressions.stringTemplate("DATE_FORMAT({0}, {1})", qBoardCommentEntity.createDt, dateFormat).as("createDt"),
                    Expressions.stringTemplate("DATE_FORMAT({0}, {1})", qBoardCommentEntity.updateDt, dateFormat).as("updateDt"),
                    qBoardCommentEntity.createUser.as("createUser"),
                    qUsersDtlEntity.name.as("createUserName"),
                    qUsersEntity.profileImgUrl.as("createUserProfileUrl")
                )
            )
            .from(qBoardCommentEntity)
            .leftJoin(qUsersDtlEntity)
                .on(qBoardCommentEntity.createUser.eq(qUsersDtlEntity.username))
            .leftJoin(qUsersEntity)
                .on(qBoardCommentEntity.createUser.eq(qUsersEntity.username))
            .where(builder)
            .orderBy(qBoardCommentEntity.createDt.desc())
            .fetch();
    }
}
