package com.safety.law.domain.admin.controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.domain.admin.model.system.EnumerationRS;
import com.safety.law.domain.admin.service.SystemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/system")
@RequiredArgsConstructor
@Slf4j
public class SystemController {

    private final SystemService systemService;


    @GetMapping("/enumeration")
    public ResponseEntity<?> getEnumerationCode(@RequestParam(name = "type") EnumerationType type){
        log.warn("type => {}", type);

        Map<String,String> enumerationMap = systemService.getEnumerationMap(type);
        EnumerationRS result = new EnumerationRS();
        result.setEnumeration(enumerationMap);

        return ResponseEntity.ok().body(result);
    }

    public static enum EnumerationType{
        CATEGORY, // 법률 카테고리
        SYSTEM_SCHEDULER, // 스케줄러
        SYSTEM_SCHEDULER_STATUS, // 스케줄러 상태
        PLATFORM, // SNS 플랫폼 타입
        MESSAGE_TYPE, // SMS 전송 타입 
        NOTIFICATION_TYPE, // [푸쉬]알림 타입
        AUTH_TYPE, // 계정 관련 상수
        LOGIN_FAIL_TYPE, // 로그인 실패타입
        LOGGING, // 로그 카테고리
        USER_TYPE, // 유저 권한타입

    }

}
