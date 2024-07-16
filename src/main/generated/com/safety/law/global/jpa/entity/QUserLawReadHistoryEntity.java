package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserLawReadHistoryEntity is a Querydsl query type for UserLawReadHistoryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserLawReadHistoryEntity extends EntityPathBase<UserLawReadHistoryEntity> {

    private static final long serialVersionUID = 723430706L;

    public static final QUserLawReadHistoryEntity userLawReadHistoryEntity = new QUserLawReadHistoryEntity("userLawReadHistoryEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> lawIdx = createNumber("lawIdx", Long.class);

    public final NumberPath<Long> userLawReadHistoryIdx = createNumber("userLawReadHistoryIdx", Long.class);

    public final StringPath username = createString("username");

    public QUserLawReadHistoryEntity(String variable) {
        super(UserLawReadHistoryEntity.class, forVariable(variable));
    }

    public QUserLawReadHistoryEntity(Path<? extends UserLawReadHistoryEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserLawReadHistoryEntity(PathMetadata metadata) {
        super(UserLawReadHistoryEntity.class, metadata);
    }

}

