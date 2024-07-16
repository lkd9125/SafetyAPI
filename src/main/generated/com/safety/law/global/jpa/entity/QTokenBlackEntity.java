package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTokenBlackEntity is a Querydsl query type for TokenBlackEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTokenBlackEntity extends EntityPathBase<TokenBlackEntity> {

    private static final long serialVersionUID = 51998641L;

    public static final QTokenBlackEntity tokenBlackEntity = new QTokenBlackEntity("tokenBlackEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final StringPath username = createString("username");

    public QTokenBlackEntity(String variable) {
        super(TokenBlackEntity.class, forVariable(variable));
    }

    public QTokenBlackEntity(Path<? extends TokenBlackEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTokenBlackEntity(PathMetadata metadata) {
        super(TokenBlackEntity.class, metadata);
    }

}

