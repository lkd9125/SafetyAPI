package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardCommentEntity is a Querydsl query type for BoardCommentEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardCommentEntity extends EntityPathBase<BoardCommentEntity> {

    private static final long serialVersionUID = 1612610180L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardCommentEntity boardCommentEntity = new QBoardCommentEntity("boardCommentEntity");

    public final NumberPath<Long> boardCommentIdx = createNumber("boardCommentIdx", Long.class);

    public final QBoardEntity boardEntity;

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final StringPath createUser = createString("createUser");

    public final NumberPath<Long> parentIdx = createNumber("parentIdx", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updateDt = createDateTime("updateDt", java.time.LocalDateTime.class);

    public final StringPath updateUser = createString("updateUser");

    public QBoardCommentEntity(String variable) {
        this(BoardCommentEntity.class, forVariable(variable), INITS);
    }

    public QBoardCommentEntity(Path<? extends BoardCommentEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardCommentEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardCommentEntity(PathMetadata metadata, PathInits inits) {
        this(BoardCommentEntity.class, metadata, inits);
    }

    public QBoardCommentEntity(Class<? extends BoardCommentEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.boardEntity = inits.isInitialized("boardEntity") ? new QBoardEntity(forProperty("boardEntity")) : null;
    }

}

