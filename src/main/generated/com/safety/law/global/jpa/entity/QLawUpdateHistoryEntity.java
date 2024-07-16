package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLawUpdateHistoryEntity is a Querydsl query type for LawUpdateHistoryEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLawUpdateHistoryEntity extends EntityPathBase<LawUpdateHistoryEntity> {

    private static final long serialVersionUID = -1804275980L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLawUpdateHistoryEntity lawUpdateHistoryEntity = new QLawUpdateHistoryEntity("lawUpdateHistoryEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final QLawEntity lawEntity;

    public final NumberPath<Long> lawUpdateHistoryIdx = createNumber("lawUpdateHistoryIdx", Long.class);

    public final StringPath newContent = createString("newContent");

    public final StringPath oldContent = createString("oldContent");

    public QLawUpdateHistoryEntity(String variable) {
        this(LawUpdateHistoryEntity.class, forVariable(variable), INITS);
    }

    public QLawUpdateHistoryEntity(Path<? extends LawUpdateHistoryEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLawUpdateHistoryEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLawUpdateHistoryEntity(PathMetadata metadata, PathInits inits) {
        this(LawUpdateHistoryEntity.class, metadata, inits);
    }

    public QLawUpdateHistoryEntity(Class<? extends LawUpdateHistoryEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lawEntity = inits.isInitialized("lawEntity") ? new QLawEntity(forProperty("lawEntity")) : null;
    }

}

