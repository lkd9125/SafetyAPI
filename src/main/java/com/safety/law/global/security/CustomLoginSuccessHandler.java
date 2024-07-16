package com.safety.law.global.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.safety.law.global.jpa.entity.LoggingEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.LoggingQueryRepository;
import com.safety.law.global.jpa.repository.UsersRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{

    private final UsersRepository usersRepository;

    private final LoggingQueryRepository loggingQueryRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        try {

            LoggingEntity logVO = new LoggingEntity();
        
            logVO.setType("LOGIN_SUCCESS");
            logVO.setUsername(authentication.getName());
            logVO.setIp(request.getRemoteAddr());        

            loggingQueryRepository.insert(logVO);
            

            UsersEntity user = (UsersEntity) usersRepository.findById(authentication.getName()).get();

            user.setEnabled(true);
            user.setPassFailCount(0);

            usersRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
}
