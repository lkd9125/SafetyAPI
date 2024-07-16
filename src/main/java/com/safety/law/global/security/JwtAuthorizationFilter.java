package com.safety.law.global.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safety.law.global.base.BaseErrorModel;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.UsersRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter{ // 그냥 필터 적용 시 servlet차원에서 1번, Security설정에서 1번으로 총 2번 호출됨
    
    private final JwtTokenProvider jwtTokenProvider;

    private final UsersRepository usersRepository;

    
    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider, UsersRepository usersRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.getHeaderToken();
        log.warn("URI => {}", request.getRequestURI());
        log.warn("JwtAuthorizationFilter - TOKEN => {}",token);

        try {
            if(token != null && jwtTokenProvider.validateToken(token)){

                String username = jwtTokenProvider.getSubject(token);
                UsersEntity userEntity = usersRepository.findById(username)
                    .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

                if(userEntity.getDeleteDt() != null) throw new AppException(ExceptionCode.USER_DELETE);

                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);  
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (AppException e) {
            handleException(response, e);
        } catch (Exception e) {
            log.error("",e);
            handleException(response, new AppException(ExceptionCode.INTERNAL_SERVER_ERROR));
        }

        
    }

    private void handleException(HttpServletResponse response, AppException exception) throws IOException{
        log.error("", exception);
        BaseErrorModel baseBody = new BaseErrorModel();
        baseBody.setCode(exception.getCode());
        baseBody.setDesc(exception.getMessage());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 상태 코드 401을 설정

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(baseBody);
        
        response.getWriter().write(responseBody);
    }
    
}
