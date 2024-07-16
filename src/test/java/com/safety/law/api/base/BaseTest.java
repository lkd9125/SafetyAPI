package com.safety.law.api.base;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safety.law.domain.user.model.register.UserRegisterRQ;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.AuthoritiesEntity;
import com.safety.law.global.jpa.entity.AuthoritiesEntity.AuthorityId;
import com.safety.law.global.jpa.entity.UsersDtlEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.JwtTokenProvider;
import com.safety.law.global.security.model.TokenModel;
import com.safety.law.global.security.model.UserType;

import jakarta.servlet.ServletException;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

public abstract class BaseTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    public MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUpAll(RestDocumentationContextProvider restDocumentationContextProvider) throws ServletException{

        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init(new MockFilterConfig(webApplicationContext.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext) // @Autowired로 빈주입 받은 context
            .addFilters(new CharacterEncodingFilter("UTF-8", true), delegateProxyFilter) // UTF-8 인코딩 필터
            .apply(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider)
                .operationPreprocessors()
                .withRequestDefaults( // (1)
                    modifyUris().scheme("http").host("localhost").port(8080), prettyPrint() // URL 정보를 넣어주시면 됩니다.
                )
                .withResponseDefaults(
                    prettyPrint() // (2)
                )
            )
            .build();
    }

    protected TokenModel getAdminToken(UsersRepository usersRepository, PasswordEncoder passwordEncoder){
        
        Optional<UsersEntity> userWrapper = usersRepository.findById("lkd9125");

        TokenModel tokenModel = new TokenModel();
        UsernamePasswordAuthenticationToken userToken = null;

        if(userWrapper.isPresent()){

            UsersEntity user = userWrapper.get();

            user.getAuthorities();

            tokenModel = jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
    
            userToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
    
        } else {
            UserRegisterRQ userRegisterRQ = new UserRegisterRQ();
            userRegisterRQ.setId("lkd9125");   
            userRegisterRQ.setPassword("1234");
            userRegisterRQ.setEmail("lkd9125@naver.com");
            userRegisterRQ.setNickname("갱갱도");
            userRegisterRQ.setHpno("01083703435");

            this.registerUsers(userRegisterRQ, UserType.ADMIN, usersRepository, passwordEncoder);

            userWrapper = usersRepository.findById("lkd9125");
            UsersEntity user = userWrapper.get();

            tokenModel = jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
    
            userToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
        }

        UsersEntity user = userWrapper.get();
        user.setRefreshToken(tokenModel.getRefreshToken());

        usersRepository.save(user);
        
        SecurityContextHolder.getContext().setAuthentication(userToken);

        return tokenModel;
    }

    // protected TokenModel getUserToken(){
    //     Optional<UsersEntity> userWrapper = usersRepository.findById("영빡이");

    //     TokenModel tokenModel = new TokenModel();
    //     UsernamePasswordAuthenticationToken userToken = null;

    //     if(userWrapper.isPresent()){

    //         UsersEntity user = userWrapper.get();

    //         tokenModel = jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
    
    //         userToken = new UsernamePasswordAuthenticationToken(
    //                 user, null, user.getAuthorities()
    //         );
    
    //     } else {
    //         UserRegisterRQ userRegisterRQ = new UserRegisterRQ();
    //         userRegisterRQ.setId("영빡이");   
    //         userRegisterRQ.setPassword("1234");
    //         userRegisterRQ.setEmail("young@naver.com");
    //         userRegisterRQ.setNickname("김영수");
    //         userRegisterRQ.setHpno("01012341234");

    //         this.registerUsers(userRegisterRQ, UserType.USER);

    //         userWrapper = usersRepository.findById("lkd9125");
    //         UsersEntity user = userWrapper.get();

    //         tokenModel = jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
    
    //         userToken = new UsernamePasswordAuthenticationToken(
    //                 user, null, user.getAuthorities()
    //         );
    //     }
        
    //     SecurityContextHolder.getContext().setAuthentication(userToken);

    //     return tokenModel;
    // }

    /**
     * 회원추가
     * @param rq
     * @return
     */
    @Transactional
    public boolean registerUsers(UserRegisterRQ rq, UserType userType, UsersRepository usersRepository, PasswordEncoder passwordEncoder) throws AppException{
        
        String username = rq.getId();
        boolean duplicateCheck = usersRepository.findById(username).isPresent();

        if(duplicateCheck) throw new AppException(ExceptionCode.DATA_DUPLICATE);

        // Users 테이블 Insert
        UsersEntity users = new UsersEntity();
        users.setUsername(username);
        users.setPassword(passwordEncoder.encode(rq.getPassword()));
        users.setEmail(rq.getEmail());
        users.setEnabled(true);
        users.setAccountNonExpired(true);
        users.setAccountNonLock(true);
        users.setPassFailCount(0);


        // UsersDtl 테이블 Insert
        UsersDtlEntity usersDtl = new UsersDtlEntity();
        usersDtl.setName(username);
        usersDtl.setUsername(username);
        usersDtl.setNickname(rq.getNickname());
        usersDtl.setPhoneNumber(rq.getHpno());

        // Users의 권한 Authorities 테이블 Insert
        AuthoritiesEntity authorities = new AuthoritiesEntity();
        AuthorityId id = new AuthorityId();
        id.setUsername(username);
        id.setAuthority(userType.getValue());
        authorities.setId(id);

        users.setUsersDtl(usersDtl);
        users.setAuthorities(authorities);

        usersRepository.save(users);

        return true;
    }

}
