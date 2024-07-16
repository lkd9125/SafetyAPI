package com.safety.law.global.security.oauth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.LoggingEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.LoggingQueryRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.JwtTokenProvider;
import com.safety.law.global.security.model.LoginRS;
import com.safety.law.global.security.model.TokenModel;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOauthSuccessHandler implements AuthenticationSuccessHandler{

    private final UsersRepository usersRepository;

    private final LoggingQueryRepository loggingQueryRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        TokenModel tokenModel = jwtTokenProvider.createToken(authentication);

        LoginRS result = new LoginRS();
        result.setToken(tokenModel);

        String jsonStr = new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);

        response.setContentType("application/json");
        response.getWriter().write(jsonStr);

        Optional<UsersEntity> usersWraper = usersRepository.findById(authentication.getName());

        if(!usersWraper.isPresent()) throw new AppException(ExceptionCode.DATA_NOT_FIND);

        UsersEntity users = usersWraper.get();

        users.setPassFailCount(0);
        users.setRefreshToken(tokenModel.getRefreshToken());

        usersRepository.save(users);

        LoggingEntity logVO = new LoggingEntity();
    
        logVO.setType("LOGIN_SUCCESS");
        logVO.setUsername(authentication.getName());
        logVO.setIp(request.getRemoteAddr());        

        loggingQueryRepository.insert(logVO);
    }

}
