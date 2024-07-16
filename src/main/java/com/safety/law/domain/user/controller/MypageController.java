package com.safety.law.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.domain.user.model.mypage.SaveBoardRS;
import com.safety.law.domain.user.service.MypageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/mypage")
public class MypageController {


    private final MypageService mypageService;

    @GetMapping("/save-board")
    public ResponseEntity<SaveBoardRS> getSaveBoard(){
        
        SaveBoardRS result = mypageService.getSaveBoard();

        return ResponseEntity.ok().body(result);
    }

}
