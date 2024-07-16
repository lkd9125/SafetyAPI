package com.safety.law.global.jpa.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.safety.law.global.jpa.entity.LoggingEntity;
import com.safety.law.global.jpa.entity.QLoggingEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LoggingQueryRepository {

    private final JPAQueryFactory query;

    /**
     * Logging할 정보를 Insert함
     * @param logging
     */
    @Transactional
    public void insert(LoggingEntity logging){

        QLoggingEntity qLogging = QLoggingEntity.loggingEntity;

        query.insert(qLogging).columns(qLogging.type, qLogging.username, qLogging.ip, qLogging.credentials, qLogging.message, qLogging.createDt)
            .values(logging.getType(), logging.getUsername(), logging.getIp(), logging.getCredentials(), logging.getMessage(), logging.getCreateDt())
            .execute();
    }
}
