package com.safety.law.global.jpa.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLawEntity is a Querydsl query type for LawEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLawEntity extends EntityPathBase<LawEntity> {

    private static final long serialVersionUID = -374392195L;

    public static final QLawEntity lawEntity = new QLawEntity("lawEntity");

    public final StringPath category = createString("category");

    public final ListPath<CommentEntity, QCommentEntity> commentList = this.<CommentEntity, QCommentEntity>createList("commentList", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DatePath<java.time.LocalDate> createDt = createDate("createDt", java.time.LocalDate.class);

    public final StringPath docId = createString("docId");

    public final StringPath highlightContent = createString("highlightContent");

    public final DatePath<java.time.LocalDate> lastUpdateDt = createDate("lastUpdateDt", java.time.LocalDate.class);

    public final NumberPath<Long> lawIdx = createNumber("lawIdx", Long.class);

    public final NumberPath<Double> score = createNumber("score", Double.class);

    public final StringPath title = createString("title");

    public final NumberPath<Long> view = createNumber("view", Long.class);

    public QLawEntity(String variable) {
        super(LawEntity.class, forVariable(variable));
    }

    public QLawEntity(Path<? extends LawEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLawEntity(PathMetadata metadata) {
        super(LawEntity.class, metadata);
    }

}

