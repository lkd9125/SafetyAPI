package com.safety.law.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.domain.user.model.cert.MessageCertRQ;
import com.safety.law.domain.user.model.login.AppLoginRQ;
import com.safety.law.domain.user.model.login.AppLoginRS;
import com.safety.law.domain.user.model.notification.NotificationAgreeRQ;
import com.safety.law.domain.user.model.profile.ProfileRS;
import com.safety.law.domain.user.model.refresh.RefreshRQ;
import com.safety.law.domain.user.model.refresh.RefreshRS;
import com.safety.law.domain.user.model.register.RegisterRS;
import com.safety.law.domain.user.model.register.UserRegisterRQ;
import com.safety.law.domain.user.model.update.UpdateUserRQ;
import com.safety.law.domain.user.model.update.ValidNicknameRS;
import com.safety.law.domain.user.service.UserService;
import com.safety.law.global.security.model.TokenModel;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    /**
     * Application Sns로그인 사용자
     * @param rq
     * @return
     */
    @PostMapping("/app/sns-login")
    public ResponseEntity<AppLoginRS> appSnsLogin(@Valid @RequestBody AppLoginRQ rq){

        TokenModel tokenModel = userService.appSnsLogin(rq);

        AppLoginRS result = new AppLoginRS();
        result.setToken(tokenModel);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 회원가입
     * @param rq 
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@Valid @RequestBody UserRegisterRQ rq){
        boolean result = userService.registerUsers(rq);

        RegisterRS rs = new RegisterRS();
        rs.setResult(result);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 유저 프로필 조회
     * @return
     */
    @GetMapping("/profile")
    public ResponseEntity<ProfileRS> profile(){

        ProfileRS result = userService.profile();

        return ResponseEntity.ok().body(result);
    }

    /**
     * 유저 프로필 업데이트
     * @param rq
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateUser(@RequestBody @Valid UpdateUserRQ rq){

        Boolean result = userService.updateUser(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 회원 탈퇴
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteUser(){

        Boolean result = userService.deleteUser();

        return ResponseEntity.ok().body(result);
    }

    /**
     * [리프레쉬]엑세스토큰 제발급
     * @param rq 리프레쉬 토큰
     * @return
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshRS> refreshToken(@RequestBody @Valid RefreshRQ rq){

        RefreshRS result = userService.refreshToken(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 인증문자 번호 확인
     * @param rq 인증번호 체크 RQ
     * @return
     */
    @PostMapping("/message/cert")
    public ResponseEntity<Boolean> messageCert(@RequestBody @Valid MessageCertRQ rq){

        Boolean result = userService.messageCert(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 알림 동의
     * @return
     */
    @PutMapping("/notification/agree")
    public ResponseEntity<Boolean> notificationAgree(@RequestBody NotificationAgreeRQ rq){

        Boolean result = userService.notificationAgree(rq);
        
        return ResponseEntity.ok().body(result);
    }

    /**
     * 닉네임 유효성 검사
     * @param nickname 검사할 닉네임
     * @return
     */
    @GetMapping("/nickname/valid")
    public ResponseEntity<ValidNicknameRS> nicknameValid(@RequestParam(name = "nickname") String nickname){

        Boolean validResult = userService.nicknameValid(nickname);
        
        ValidNicknameRS result = new ValidNicknameRS();
        result.setResult(validResult);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/logout")
    public ResponseEntity<Boolean> logout(){

        Boolean result = userService.logout();

        return ResponseEntity.ok().body(result);
    }
        
    
}
