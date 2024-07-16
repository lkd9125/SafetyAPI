package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMessageEntity is a Querydsl query type for MessageEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMessageEntity extends EntityPathBase<MessageEntity> {

    private static final long serialVersionUID = 1597148450L;

    public static final QMessageEntity messageEntity = new QMessageEntity("messageEntity");

    public final StringPath certNum = createString("certNum");

    public final BooleanPath certYn = createBoolean("certYn");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createDt = createDateTime("createDt", java.time.LocalDateTime.class);

    public final NumberPath<Long> messageIdx = createNumber("messageIdx", Long.class);

    public final StringPath phoneNum = createString("phoneNum");

    public final EnumPath<MessageEntity.MessageType> type = createEnum("type", MessageEntity.MessageType.class);

    public QMessageEntity(String variable) {
        super(MessageEntity.class, forVariable(variable));
    }

    public QMessageEntity(Path<? extends MessageEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessageEntity(PathMetadata metadata) {
        super(MessageEntity.class, metadata);
    }

}

