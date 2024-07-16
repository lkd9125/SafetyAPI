package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoardHeartReferenceEntity is a Querydsl query type for BoardHeartReferenceEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardHeartReferenceEntity extends EntityPathBase<BoardHeartReferenceEntity> {

    private static final long serialVersionUID = 901950534L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoardHeartReferenceEntity boardHeartReferenceEntity = new QBoardHeartReferenceEntity("boardHeartReferenceEntity");

    public final QBoardEntity boardEntity;

    public final NumberPath<Long> boardHeartRefrenceIdx = createNumber("boardHeartRefrenceIdx", Long.class);

    public final QUsersEntity usersEntity;

    public QBoardHeartReferenceEntity(String variable) {
        this(BoardHeartReferenceEntity.class, forVariable(variable), INITS);
    }

    public QBoardHeartReferenceEntity(Path<? extends BoardHeartReferenceEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoardHeartReferenceEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoardHeartReferenceEntity(PathMetadata metadata, PathInits inits) {
        this(BoardHeartReferenceEntity.class, metadata, inits);
    }

    public QBoardHeartReferenceEntity(Class<? extends BoardHeartReferenceEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.boardEntity = inits.isInitialized("boardEntity") ? new QBoardEntity(forProperty("boardEntity")) : null;
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity"), inits.get("usersEntity")) : null;
    }

}

