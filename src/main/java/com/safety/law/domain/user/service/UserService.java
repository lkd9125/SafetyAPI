package com.safety.law.domain.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.domain.user.constant.PlatformConstant;
import com.safety.law.domain.user.model.cert.MessageCertRQ;
import com.safety.law.domain.user.model.login.AppLoginRQ;
import com.safety.law.domain.user.model.notification.NotificationAgreeRQ;
import com.safety.law.domain.user.model.profile.ProfileRS;
import com.safety.law.domain.user.model.refresh.RefreshRQ;
import com.safety.law.domain.user.model.refresh.RefreshRS;
import com.safety.law.domain.user.model.register.UserRegisterRQ;
import com.safety.law.domain.user.model.update.UpdateUserRQ;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.AuthoritiesEntity;
import com.safety.law.global.jpa.entity.AuthoritiesEntity.AuthorityId;
import com.safety.law.global.jpa.entity.LoggingEntity;
import com.safety.law.global.jpa.entity.MessageEntity;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity;
import com.safety.law.global.jpa.entity.TokenBlackEntity;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity.NotificationType;
import com.safety.law.global.jpa.entity.UsersDtlEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.repository.LoggingQueryRepository;
import com.safety.law.global.jpa.repository.MessageQueryRepository;
import com.safety.law.global.jpa.repository.MessageRepository;
import com.safety.law.global.jpa.repository.NotificationAgreeQueryRepository;
import com.safety.law.global.jpa.repository.NotificationAgreeRepository;
import com.safety.law.global.jpa.repository.TokenBlackRepository;
import com.safety.law.global.jpa.repository.UserLawReadHistoryRepository;
import com.safety.law.global.jpa.repository.UsersDtlRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.JwtTokenProvider;
import com.safety.law.global.security.Principal;
import com.safety.law.global.security.model.TokenModel;
import com.safety.law.global.security.model.UserType;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;

    private final UsersDtlRepository usersDtlRepository;

    private final UserLawReadHistoryRepository userLawReadHistoryRepository;

    private final TokenBlackRepository tokenBlackRepository;

    private final MessageRepository messageRepository;

    private final MessageQueryRepository messageQueryRepository;

    private final LoggingQueryRepository loggingQueryRepository;

    private final NotificationAgreeRepository notificationAgreeRepository;

    private final NotificationAgreeQueryRepository notificationAgreeQueryRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final HttpServletRequest request;

    private Short SMS_AUTH_TIME = 5; // 문자 인증시간 5분


    /**
     * 회원추가
     * @param rq
     * @return
     */
    @Transactional
    public boolean registerUsers(UserRegisterRQ rq) throws AppException{
        
        String username = rq.getId();
        boolean duplicateCheck = usersRepository.findById(username).isPresent();

        if(duplicateCheck) throw new AppException(ExceptionCode.DATA_DUPLICATE);

        // Users 테이블 Insert
        UsersEntity users = UsersEntity.builder()
        .username(username)
        .password(passwordEncoder.encode(rq.getPassword()))
        .email(rq.getEmail())
        .enabled(true)
        .accountNonExpired(true)
        .accountNonLock(true)
        .passFailCount(0)
        .createDt(LocalDateTime.now())
        .updateDt(LocalDateTime.now())
        .build();

        usersRepository.save(users);

        // UsersDtl 테이블 Insert
        UsersDtlEntity usersDtl = UsersDtlEntity.builder()
        .username(username)
        .name(rq.getName())
        .nickname(rq.getNickname() != null ? rq.getNickname() : rq.getName())
        .phoneNumber(rq.getHpno())
        .build();

        // Users의 권한 Authorities 테이블 Insert
        AuthoritiesEntity authorities = AuthoritiesEntity.builder()
        .id(AuthorityId.builder()
            .username(username)
            .authority(UserType.USER.getValue())
            .build()
        )
        .build();

        users.setUsersDtl(usersDtl);
        users.setAuthorities(authorities);

        usersRepository.save(users);

        return true;
    }

    /**
     * AppLogin
     * @param rq
     * @return
     */
    @Transactional
    public TokenModel appSnsLogin(AppLoginRQ rq) {

        String platform = rq.getPlatform().replace(" ", "");
        
        PlatformConstant.platformValid(platform);
        
        rq.setId(platform + "_" + rq.getId());

        UsersEntity users = this.oAuth2RegisterUsers(rq);
        
        users.getAuthorities();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(users.getUsername(), "", users.getAuthorities());

        TokenModel result = jwtTokenProvider.createToken(usernamePasswordAuthenticationToken);

        users.setPassFailCount(0);
        users.setRefreshToken(result.getRefreshToken());

        usersRepository.save(users);

        LoggingEntity logVO = new LoggingEntity();
    
        logVO.setType("LOGIN_SUCCESS");
        logVO.setUsername(usernamePasswordAuthenticationToken.getName());
        logVO.setIp(request.getRemoteAddr());        

        loggingQueryRepository.insert(logVO);

        return result;
    }

    private UsersEntity oAuth2RegisterUsers(AppLoginRQ rq) throws AppException{
        
        String username = rq.getId();
        Optional<UsersEntity> userWrapper = usersRepository.findById(username);

        if(userWrapper.isPresent()) {
            // TODO:: 탈퇴 후 7일이 지났으면 로그인 가능함
            UsersEntity usersEntity = userWrapper.get();            

            if(usersEntity.getDeleteDt() != null){

                LocalDateTime deleteTime = usersEntity.getDeleteDt();
                LocalDateTime now = LocalDateTime.now().minusDays(7);

                if(!deleteTime.isBefore(now)){
                    // User의 DeleteDT가 채워져 있고, 7일이 지나지 않았으면 로그인 X 
                    throw new AppException(ExceptionCode.USER_DELETE);     
                }
            }

            return usersEntity;
        }

        
        // Users 테이블 Insert
        UsersEntity users = new UsersEntity();
        users.setUsername(username);
        users.setPassword(passwordEncoder.encode(rq.getPlatform() + "_" + System.currentTimeMillis()));
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
        if(rq.getMobile() != null)usersDtl.setPhoneNumber(rq.getMobile().replace("-", ""));

        // Users의 권한 Authorities 테이블 Insert
        AuthoritiesEntity authorities = new AuthoritiesEntity();
        AuthorityId id = new AuthorityId();
        id.setUsername(username);
        id.setAuthority(UserType.USER.getValue());
        authorities.setId(id);

        users.setUsersDtl(usersDtl);
        users.setAuthorities(authorities);

        return usersRepository.save(users);
    }

    @Transactional
    public Boolean updateUser(@Valid UpdateUserRQ rq) {

        String newNickname = rq.getNickname();

        Long nickValid = usersDtlRepository.countByNickname(rq.getNickname());
        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        if(nickValid > 0) {
            String oldNickname = usersEntity.getUsersDtl().getNickname();
            
            if(!oldNickname.equals(newNickname)){
                throw new AppException(ExceptionCode.DATA_DUPLICATE);
            }
        }

        UsersDtlEntity usersDtlEntity = usersEntity.getUsersDtl();

        if(newNickname != null) usersDtlEntity.setNickname(newNickname);
        if(rq.getProfileUrl() != null) usersEntity.setProfileImgUrl(rq.getProfileUrl());

        usersEntity.setUsersDtl(usersDtlEntity);

        return true;
    }

    /**
     * 회원 탈퇴 TODO :: 추후 회원탈퇴 로직은 추가 될 예정임
     * @return
     */
    @Transactional
    public Boolean deleteUser() {

        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        userLawReadHistoryRepository.deleteByUsername(username);

        usersEntity.setDeleteDt(LocalDateTime.now());

        return true;
    }

    /**
     * [리프레쉬]엑세스토큰 제발급
     * @param rq 리프레쉬 토큰
     * @return
     */
    public RefreshRS refreshToken(@Valid RefreshRQ rq) {

        try {
            String refreshToken = rq.getRefreshToken();

            String username = jwtTokenProvider.getSubject(refreshToken);

            UsersEntity usersEntity = usersRepository.findById(username)
                .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

            boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);
            boolean refreshTokenDBValid = usersEntity.getRefreshToken().equals(rq.getRefreshToken());
            boolean refreshTokenBlackValid = tokenBlackRepository.countByUsernameAndRefreshToken(usersEntity.getUsername(), refreshToken) == 0 ? true : false;

            boolean refreshCheck = refreshTokenValid && refreshTokenDBValid && refreshTokenBlackValid; // 리프레쉬토큰 검증과, refreshTokenBlackValid 둘다 통과해야 Access토큰 재발급

            if(!refreshCheck) throw new AppException(ExceptionCode.SING_IN_FROM_ANOTHER_DEVICE);

            TokenModel tokenModel = jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(usersEntity.getUsername(), usersEntity.getPassword(), usersEntity.getAuthorities()));

            RefreshRS result = new RefreshRS();
            result.setAccessToken(tokenModel.getAccessToken());

            return result;

        } catch (Exception e) {
            if(e instanceof ExpiredJwtException){
                throw new AppException(ExceptionCode.REFRESH_JWT_EXPIRED);
            }
            throw e;
        }
        
    }
    
    /**
     * 메세지 인증
     * @param rq 휴대폰번호, 인증번호, 타입 RQ
     * @return
     */
    @Transactional
    public Boolean messageCert(@Valid MessageCertRQ rq) {

        MessageEntity entity = messageQueryRepository.findByPhoneNumTop1(rq.getPhoneNum(), rq.getType());

        if(entity.getCertYn()) throw new AppException(ExceptionCode.ALREADY_CERT);

        LocalDateTime now = LocalDateTime.now().minusMinutes(SMS_AUTH_TIME);
        LocalDateTime certTime = entity.getCreateDt();

        boolean timeCheck = now.isBefore(certTime);

        if(!timeCheck) throw new AppException(ExceptionCode.TIME_OUT);
        if(!(entity.getCertNum().equals(rq.getCertNum()))) throw new AppException(ExceptionCode.NOT_AUTHENTICATION_USER);

        entity.setCertYn(true);

        messageRepository.save(entity);

        return true;
    }

    /**
     * 유저 프로필 조회
     * @return
     */
    public ProfileRS profile() {

        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        UsersDtlEntity usersDtlEntity = usersEntity.getUsersDtl();

        ProfileRS result = new ProfileRS();
        result.setEmail(usersEntity.getEmail());
        result.setMobile(usersDtlEntity.getPhoneNumber());
        result.setName(usersDtlEntity.getName());
        result.setNickname(usersDtlEntity.getNickname());

        return result;
    }

    /**
     * 알림 추가
     * @param rq
     * @return
     */
    @Transactional
    public Boolean notificationAgree(NotificationAgreeRQ rq) {

        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        usersEntity.setFcmDeviceToken(rq.getFcmDeviceToken());

        // TRUE : 알림 추가가능 , FALSE : 알림 추가 불가
        Boolean notificationValid =  notificationAgreeQueryRepository.notificationValid(usersEntity.getUsername(), rq.getNotificationType());

        if(notificationValid){
            List<NotificationAgreeEntity> notificationAgreeEntities = new ArrayList<>();

            for(NotificationType type : rq.getNotificationType()){
                NotificationAgreeEntity node = NotificationAgreeEntity.builder()
                    .notificationType(type)
                    .usersEntity(usersEntity)
                    .build();

                notificationAgreeEntities.add(node);
            }

            notificationAgreeRepository.saveAll(notificationAgreeEntities);
        }

        return true;
    }

    /**
     * 닉네임 유효성검사
     * @param nickname
     * @return
     */
    public Boolean nicknameValid(String nickname) {
        Long nickValid = usersDtlRepository.countByNickname(nickname);

        return nickValid < 1;
    }

    /**
     * 로그아웃
     * @return
     */
    @Transactional
    public Boolean logout() {

        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        Long count = tokenBlackRepository.countByUsernameAndRefreshToken(usersEntity.getUsername(), usersEntity.getRefreshToken());

        if(count < 1){
            TokenBlackEntity tokenBlackEntity = TokenBlackEntity.builder()
                .username(usersEntity.getUsername())
                .refreshToken(usersEntity.getRefreshToken())
                .build();
    
    
            tokenBlackRepository.save(tokenBlackEntity);
        }

        return true;
    }


}
