package com.safety.law.domain.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.domain.common.model.message.SendMessageRQ;
import com.safety.law.domain.common.service.CommonService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/v1/common")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    /**
     * 인증 메세지 발송
     * @param rq 인증 번호 Message RQ
     * @return
     */
    @PostMapping("/message/send")
    public ResponseEntity<Boolean> messageSend(@RequestBody @Valid SendMessageRQ rq){

        Boolean result = commonService.certMessageSend(rq);

        return ResponseEntity.ok().body(result);
    }

}
