package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardEntity is a Querydsl query type for BoardEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardEntity extends EntityPathBase<BoardEntity> {

    private static final long serialVersionUID = -374911295L;

    public static final QBoardEntity boardEntity = new QBoardEntity("boardEntity");

    public final ListPath<BoardCommentEntity, QBoardCommentEntity> boardCommentList = this.<BoardCommentEntity, QBoardCommentEntity>createList("boardCommentList", BoardCommentEntity.class, QBoardCommentEntity.class, PathInits.DIRECT2);

    public final ListPath<BoardHeartReferenceEntity, QBoardHeartReferenceEntity> boardHeartReferenceCommentList = this.<BoardHeartReferenceEntity, QBoardHeartReferenceEntity>createList("boardHeartReferenceCommentList", BoardHeartReferenceEntity.class, QBoardHeartReferenceEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> boardIdx = createNumber("boardIdx", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final StringPath createUser = createString("createUser");

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final StringPath updateUser = createString("updateUser");

    public QBoardEntity(String variable) {
        super(BoardEntity.class, forVariable(variable));
    }

    public QBoardEntity(Path<? extends BoardEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardEntity(PathMetadata metadata) {
        super(BoardEntity.class, metadata);
    }

}

