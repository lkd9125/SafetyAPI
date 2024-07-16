package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationAgreeEntity is a Querydsl query type for NotificationAgreeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationAgreeEntity extends EntityPathBase<NotificationAgreeEntity> {

    private static final long serialVersionUID = 1300402204L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationAgreeEntity notificationAgreeEntity = new QNotificationAgreeEntity("notificationAgreeEntity");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deleteDt = createDateTime("deleteDt", java.time.LocalDateTime.class);

    public final EnumPath<NotificationAgreeEntity.NotificationType> notificationType = createEnum("notificationType", NotificationAgreeEntity.NotificationType.class);

    public final NumberPath<Long> pushNotificationAgreeIdx = createNumber("pushNotificationAgreeIdx", Long.class);

    public final QUsersEntity usersEntity;

    public QNotificationAgreeEntity(String variable) {
        this(NotificationAgreeEntity.class, forVariable(variable), INITS);
    }

    public QNotificationAgreeEntity(Path<? extends NotificationAgreeEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationAgreeEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationAgreeEntity(PathMetadata metadata, PathInits inits) {
        this(NotificationAgreeEntity.class, metadata, inits);
    }

    public QNotificationAgreeEntity(Class<? extends NotificationAgreeEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.usersEntity = inits.isInitialized("usersEntity") ? new QUsersEntity(forProperty("usersEntity"), inits.get("usersEntity")) : null;
    }

}

