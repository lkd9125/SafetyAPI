package com.safety.law.global.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safety.law.global.base.BaseErrorModel;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.LoggingEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.LoggingQueryRepository;
import com.safety.law.global.jpa.repository.UsersRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private UsersRepository usersRepository;

    private LoggingQueryRepository loggingQueryRepository;

    public CustomLoginFailureHandler(UsersRepository usersRepository, LoggingQueryRepository loggingQueryRepository){
        this.usersRepository = usersRepository;
        this.loggingQueryRepository = loggingQueryRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");

        log.warn("exception => {}", exception.getMessage());
        log.warn("username => {}", username);

        FailType failType = FailType.fromMessage(exception.getMessage());

        LoggingEntity logging = new LoggingEntity();
        logging.setIp(request.getRemoteAddr());

        String code = ExceptionCode.DATA_NOT_FIND.code();
        String desc = ExceptionCode.DATA_NOT_FIND.message();


        switch (failType) {
            case USER_NOT_FOUNT:
                logging.setType(LoggingType.LOGIN_NOT_USER.getValue());
                logging.setMessage("유저를 찾을 수 없습니다.");
                break;
            case ACOUNT_DISABLE:
                logging.setType(LoggingType.LOGIN_DISABLED.getValue());
                logging.setMessage("계정이 비활성화 되었습니다.");
                break;
            case ACOUNT_EXPRIED:
                logging.setType(LoggingType.LOGIN_EXPIRED.getValue());
                logging.setMessage("계정이 만료되었습니다.");
                logging.setCredentials("[PROTECT PASSWORD]");
                break;
            case ACOUNT_LOCK:
                logging.setType(LoggingType.LOGIN_LOCKED.getValue());
                logging.setMessage("계정이 잠겼습니다.");
                logging.setCredentials("[PROTECT PASSWORD]");
                break;
            case PERMISSION_MISMATCH:
                logging.setType(LoggingType.LOGIN_PERMISSION_MISMATCH.getValue());
                logging.setMessage("권한이 일치하지 않습니다.");
                logging.setCredentials("[PROTECT PASSWORD]");

                code = ExceptionCode.NOT_PERMISSION.code();
                desc = ExceptionCode.NOT_PERMISSION.message();
                break;
            case PASS_NOT_MATCH:
                logging.setType(LoggingType.LOGIN_WRONG_PW.getValue());
                logging.setMessage("비밀번호를 틀렸습니다.");

                UsersEntity users = usersRepository.findById(username).get();

                int loginFailCount = users.getPassFailCount();

                if(loginFailCount >= 4){
                    users.setAccountNonLock(false);
                } else {
                    users.setAccountNonLock(true);
                }

                users.setPassFailCount(loginFailCount+1);
                usersRepository.save(users);
                break;
            case USER_DELETE:
                logging.setType(LoggingType.DELETE_USER_LOGIN.getValue());
                logging.setMessage("탈퇴한 회원이 로그인을 시도했습니다.");

                code = ExceptionCode.USER_DELETE.code();
                desc = ExceptionCode.USER_DELETE.message();

                break;
        }

        loggingQueryRepository.insert(logging);

        BaseErrorModel baseBody = new BaseErrorModel();
        baseBody.setCode(code);
        baseBody.setDesc(desc);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(baseBody);

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 상태 코드 401을 설정
        response.getWriter().write(responseBody);
    }
    
}
