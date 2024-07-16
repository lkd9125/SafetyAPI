package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDayRankEntity is a Querydsl query type for DayRankEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDayRankEntity extends EntityPathBase<DayRankEntity> {

    private static final long serialVersionUID = -230492605L;

    public static final QDayRankEntity dayRankEntity = new QDayRankEntity("dayRankEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> dayRankIdx = createNumber("dayRankIdx", Long.class);

    public final StringPath keyword = createString("keyword");

    public QDayRankEntity(String variable) {
        super(DayRankEntity.class, forVariable(variable));
    }

    public QDayRankEntity(Path<? extends DayRankEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDayRankEntity(PathMetadata metadata) {
        super(DayRankEntity.class, metadata);
    }

}

