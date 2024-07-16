package com.safety.law.global.security.oauth;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.AuthoritiesEntity;
import com.safety.law.global.jpa.entity.AuthoritiesEntity.AuthorityId;
import com.safety.law.global.jpa.entity.UsersDtlEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.model.UserType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService{
    
    private final UsersRepository usersRepository;

    // private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        log.warn("userRequest => {}", request);
        OAuth2User oAuth2User = super.loadUser(request);
        try {
            log.warn("==> {}", new ObjectMapper().writeValueAsString(oAuth2User));
        } catch (Exception e) {
            log.error("", e);
        }

        String clientName = request.getClientRegistration().getClientName();

        OAuthAttributes userInfo = OAuthAttributes.getOauthUserInfo(oAuth2User, request);

        if(userInfo.isEmpty()) throw new OAuth2AuthenticationException("UserInfoObjectException");

        String prefix = clientName + "_";

        userInfo.setUsername(prefix + userInfo.getUsername());

        this.oAuth2RegisterUsers(userInfo);
        
        return new CustomOAuth2User(userInfo.getUsername());
    }

    private boolean oAuth2RegisterUsers(OAuthAttributes rq) throws AppException{
        
        String username = rq.getUsername();
        // boolean duplicateCheck = usersRepository.findById(username).isPresent();
        Optional<UsersEntity> userWrapper = usersRepository.findById(username);

        if(userWrapper.isPresent()) {
        
            UsersEntity usersEntity = userWrapper.get();            
            if(usersEntity.getDeleteDt() != null) throw new AppException(ExceptionCode.USER_DELETE); // User의 DeleteDT가 채워져 있으면 로그인 X

            return true;
            
        }

        // Users 테이블 Insert
        UsersEntity users = new UsersEntity();
        users.setUsername(username);
        users.setPassword(rq.getPlatform() + "_" + System.currentTimeMillis());
        users.setEmail(rq.getEmail());
        users.setEnabled(true);
        users.setAccountNonExpired(true);
        users.setAccountNonLock(true);
        users.setPassFailCount(0);
        users.setCreateDt(LocalDateTime.now());
        users.setUpdateDt(LocalDateTime.now());

        usersRepository.save(users);

        // UsersDtl 테이블 Insert
        UsersDtlEntity usersDtl = new UsersDtlEntity();
        usersDtl.setUsername(username);
        usersDtl.setName(rq.getName());
        usersDtl.setNickname(rq.getNickname() != null ? rq.getNickname() : rq.getName());
        usersDtl.setPhoneNumber(rq.getHpno());

        // Users의 권한 Authorities 테이블 Insert
        AuthoritiesEntity authorities = new AuthoritiesEntity();
        AuthorityId id = new AuthorityId();
        id.setUsername(username);
        id.setAuthority(UserType.USER.getValue());
        authorities.setId(id);

        users.setUsersDtl(usersDtl);
        users.setAuthorities(authorities);

        usersRepository.save(users);

        return true;
    }

}
