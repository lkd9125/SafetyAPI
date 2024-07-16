package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.global.jpa.entity.QNotificationAgreeEntity;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationAgreeQueryRepository {

    private final JPAQueryFactory query;

    /**
     * 
     * @param username 회원 아이디
     * @param types 알림 타입들
     * @return
     */
    public Boolean notificationValid(String username, List<NotificationType> types){

        QNotificationAgreeEntity qNotificationAgreeEntity = QNotificationAgreeEntity.notificationAgreeEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qNotificationAgreeEntity.usersEntity.username.eq(username));
        builder.and(qNotificationAgreeEntity.notificationType.in(types));

        Long count = query
            .select(
                qNotificationAgreeEntity.count()
            )
            .from(qNotificationAgreeEntity)
            .where(builder)
            .fetchOne()
            ;

        return count < 1;
    }
}
