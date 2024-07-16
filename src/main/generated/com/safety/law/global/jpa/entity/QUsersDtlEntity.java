package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUsersDtlEntity is a Querydsl query type for UsersDtlEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUsersDtlEntity extends EntityPathBase<UsersDtlEntity> {

    private static final long serialVersionUID = 1734936671L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUsersDtlEntity usersDtlEntity = new QUsersDtlEntity("usersDtlEntity");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath username = createString("username");

    public final QUsersEntity usersEntity;

    public QUsersDtlEntity(String variable) {
        this(UsersDtlEntity.class, forVariable(variable), INITS);
    }

    public QUsersDtlEntity(Path<? extends UsersDtlEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUsersDtlEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUsersDtlEntity(PathMetadata metadata, PathInits inits) {
        this(UsersDtlEntity.class, metadata, inits);
    }

    public QUsersDtlEntity(Class<? extends UsersDtlEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity"), inits.get("usersEntity")) : null;
    }

}

