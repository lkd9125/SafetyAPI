package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSchedulerUpdateDateEntity is a Querydsl query type for SchedulerUpdateDateEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedulerUpdateDateEntity extends EntityPathBase<SchedulerUpdateDateEntity> {

    private static final long serialVersionUID = 977714189L;

    public static final QSchedulerUpdateDateEntity schedulerUpdateDateEntity = new QSchedulerUpdateDateEntity("schedulerUpdateDateEntity");

    public final StringPath name = createString("name");

    public final StringPath status = createString("status");

    public final DatePath<java.time.LocalDate> updateDt = createDate("updateDt", java.time.LocalDate.class);

    public QSchedulerUpdateDateEntity(String variable) {
        super(SchedulerUpdateDateEntity.class, forVariable(variable));
    }

    public QSchedulerUpdateDateEntity(Path<? extends SchedulerUpdateDateEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSchedulerUpdateDateEntity(PathMetadata metadata) {
        super(SchedulerUpdateDateEntity.class, metadata);
    }

}

