package com.safety.law.domain.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.global.common.service.CommonFcmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/common/test")
public class CommonTestController {

    private final CommonFcmService commonFcmService;

    @GetMapping("/fcm-access")
    public ResponseEntity<Boolean> test() throws Exception{

        // Boolean result = commonFcmService.fcmTest();

        return ResponseEntity.ok().body(null);
    }


}
