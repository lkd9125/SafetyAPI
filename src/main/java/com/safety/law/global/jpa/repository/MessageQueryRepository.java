package com.safety.law.global.jpa.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.domain.user.model.cert.MessageCertRQ;
import com.safety.law.global.jpa.entity.MessageEntity;
import com.safety.law.global.jpa.entity.QMessageEntity;
import com.safety.law.global.jpa.entity.MessageEntity.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MessageQueryRepository {

    private final JPAQueryFactory query;

    public MessageEntity findByPhoneNumTop1(String phoneNum, MessageType type){

        QMessageEntity messageEntity = QMessageEntity.messageEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(messageEntity.phoneNum.eq(phoneNum.replace("-", "")));
        builder.and(messageEntity.type.eq(type));

        return query
            .select(messageEntity)
            .from(messageEntity)
            .where(builder)
            .orderBy(messageEntity.createDt.desc())
            .limit(1)
            .fetchOne();
    }

}
