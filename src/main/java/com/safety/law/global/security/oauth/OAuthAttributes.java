package com.safety.law.global.security.oauth;

import java.lang.reflect.Field;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.util.JsonUtils;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OAuthAttributes {

    private String username;

    private String nickname;

    private String name;

    private String email;

    private String hpno;

    private String platform;


    public static OAuthAttributes getOauthUserInfo(OAuth2User oAuth2User, OAuth2UserRequest request){

        String platformName = request.getClientRegistration().getClientName();

        OAuthAttributes result = switch(platformName) {
            
            case "naver" -> {
                yield OAuthAttributes.getNaverOauthUser(oAuth2User);
            }
            
            default -> throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
        };

        return result;
    }

    private static OAuthAttributes getNaverOauthUser(OAuth2User oAuth2User){

        NaverAttribute naverAttribute = new NaverAttribute();

        try {
            String jsonStr = JsonUtils.getInstance().writeValueAsString(oAuth2User.getAttributes().get("response"));
            naverAttribute = JsonUtils.getInstance().readValue(jsonStr, NaverAttribute.class);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }

        if(naverAttribute != null){

            OAuthAttributes result = new OAuthAttributes();
            result.setUsername(naverAttribute.getId());
            result.setName(naverAttribute.getName());
            result.setEmail(naverAttribute.getEmail());
            result.setNickname(naverAttribute.getNickname());
            result.setHpno(naverAttribute.getMobile());
            result.setPlatform("naver");
            
            return result;
        } else {
            return null;
        }
    }

    /**
     * 객체 값 비어있는지 확인
     * @return
     */
    public Boolean isEmpty() {
        Field[] fields = this.getClass().getDeclaredFields();
    
        for (Field field : fields) {

            String fieldName = field.getName();

            if(fieldName.equals("log")) continue;

            field.setAccessible(true);

            try {

                Object fieldValue = field.get(this); // 필드 값을 Object로 가져옴
    
                log.warn("fieldName => {}, value => {}", fieldName, fieldValue);
    
                // null 값이거나, 필드 타입이 String이고 필드 값이 비어있는 경우 true 반환
                if (fieldValue == null || (field.getType().equals(String.class) && ((String) fieldValue).isEmpty())) {
                    return true;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("", e);
            }
        }
    
        return false;
    }

    @Data
    static class NaverAttribute{
        private String id;

        private String email;

        private String mobile;

        private String mobile_e164;

        private String nickname;

        private String name;
    }
}
