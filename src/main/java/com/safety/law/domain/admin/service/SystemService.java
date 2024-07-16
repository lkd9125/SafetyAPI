package com.safety.law.domain.admin.service;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.safety.law.domain.admin.controller.SystemController.EnumerationType;
import com.safety.law.domain.law.constant.CategoryConstant;
import com.safety.law.domain.scheduler.constant.SchedulerConstant;
import com.safety.law.domain.scheduler.constant.StatusConstant;
import com.safety.law.domain.user.constant.PlatformConstant;
import com.safety.law.global.jpa.entity.MessageEntity.MessageType;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity.NotificationType;
import com.safety.law.global.security.AuthConstants;
import com.safety.law.global.security.FailType;
import com.safety.law.global.security.LoggingType;
import com.safety.law.global.security.model.UserType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemService {

    public Map<String,String> getEnumerationMap(EnumerationType type){

        Map<String,String> result = new HashMap<>();

        switch (type) {
            case CATEGORY:
                result = Arrays.stream(CategoryConstant.values())
                    .collect(Collectors.toMap(CategoryConstant::name,CategoryConstant::getDescription));
                break;
            case AUTH_TYPE:
                result = Arrays.stream(AuthConstants.values())
                    .collect(Collectors.toMap(AuthConstants::name,AuthConstants::getDescription));
                break;
            case LOGGING:
                result = Arrays.stream(LoggingType.values())
                    .collect(Collectors.toMap(LoggingType::name,LoggingType::getDescription));
                break;
            case LOGIN_FAIL_TYPE:
                result = Arrays.stream(FailType.values())
                    .collect(Collectors.toMap(FailType::name,FailType::getDescription));
                break;
            case MESSAGE_TYPE:
                result = Arrays.stream(MessageType.values())
                    .collect(Collectors.toMap(MessageType::name,MessageType::getDescription));
                break;
            case NOTIFICATION_TYPE:
                result = Arrays.stream(NotificationType.values())
                    .collect(Collectors.toMap(NotificationType::name,NotificationType::getDescription));
                break;
            case PLATFORM:
                result = Arrays.stream(PlatformConstant.values())
                    .collect(Collectors.toMap(PlatformConstant::name,PlatformConstant::getDescription));
                break;
            case SYSTEM_SCHEDULER:
                result = Arrays.stream(SchedulerConstant.values())
                    .collect(Collectors.toMap(SchedulerConstant::name,SchedulerConstant::getDescription));
                break;
            case SYSTEM_SCHEDULER_STATUS:
                result = Arrays.stream(StatusConstant.values())
                    .collect(Collectors.toMap(StatusConstant::name,StatusConstant::getDescription));
                break;
            case USER_TYPE:
                result = Arrays.stream(UserType.values())
                    .collect(Collectors.toMap(UserType::name,UserType::getDescription));
                break;
        }

        return result;
    }

}
