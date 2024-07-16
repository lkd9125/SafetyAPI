package com.safety.law.api.feature.user;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.api.base.BaseTest;
import com.safety.law.domain.common.model.message.SendMessageRQ;
import com.safety.law.domain.common.service.CommonService;
import com.safety.law.global.jpa.entity.MessageEntity;
import com.safety.law.global.jpa.entity.MessageEntity.MessageType;
import com.safety.law.global.jpa.repository.MessageQueryRepository;
import com.safety.law.global.jpa.repository.MessageRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.model.TokenModel;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class UserTest extends BaseTest{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MessageQueryRepository messageQueryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommonService commonService;

    private final String BASE_URL = "/api/v1/user";

    @Test
    @Transactional
    public void logout() throws Exception{
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/logout")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "user/logout",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void nicknameValid() throws Exception{

        String nickname = "test123";

        this.mockMvc.perform(
            get(this.BASE_URL + "/nickname/valid")
            .contextPath("/api")
            .param("nickname", nickname)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "user/nickname/valid",
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("nickname").description("검사할 닉네임")
                ),
                responseFields(
                    fieldWithPath("result").type(JsonFieldType.BOOLEAN).description("결과[TRUE: 사용가능, FALSE: 사용 불가능]")
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void notificationAgree() throws Exception{
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "fcmDeviceToken" : "test_fcm_device_token",
                "notificationType" : [ "QNA_NOTIFICATION" ]
            }
        """;

        this.mockMvc.perform(
            put(this.BASE_URL + "/notification/agree")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "user/notification/agree",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("fcmDeviceToken").type(JsonFieldType.STRING).description("파이어베이스에서 발급받은 FcmDeviceToken"),
                    fieldWithPath("notificationType").type(JsonFieldType.ARRAY).description("알림동의 타입[ QNA_NOTIFICATION : QnA게시물 답글 달렸을 경우 알림 ]")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void profile() throws Exception{
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/profile")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "user/profile",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                responseFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                    fieldWithPath("mobile").type(JsonFieldType.STRING).description("휴대폰번호"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("profileUrl").type(JsonFieldType.STRING).description("유저 프로필 이미지").optional()
                )
            )
        )
        .andExpect(status().isOk())
        ;
    }

    @Test
    @Transactional
    public void messageCert() throws Exception{

        String phoneNum = "01083703435";

        SendMessageRQ sendMessageRQ = new SendMessageRQ();
        sendMessageRQ.setPhoneNum(phoneNum);
        sendMessageRQ.setType(MessageType.LINK_CERT);

        commonService.certMessageSend(sendMessageRQ);

        MessageEntity entity = messageQueryRepository.findByPhoneNumTop1(phoneNum, MessageType.LINK_CERT);
        
        String certNum = entity.getCertNum();

        String jsonRQ = """
            {
                "phoneNum" : "%s",
                "certNum" : "%s",
                "type" : "LINK_CERT"
            }        
        """.formatted(phoneNum, certNum);

        this.mockMvc.perform(
            post(this.BASE_URL + "/message/cert")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "user/message/cert",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("phoneNum").type(JsonFieldType.STRING).description("전화번호"),
                    fieldWithPath("certNum").type(JsonFieldType.STRING).description("문자로 받은 인증번호"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description("전송 타입 [LINK_CERT: 계정연동용 인증문자, UPDATE_CERT: 업데이트 인증문자, GUIDE: 안내]")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    public void refreshToken() throws Exception{
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "refreshToken" : "%s"
            }  
        """.formatted(tokenModel.getRefreshToken());

        this.mockMvc.perform(
            post(this.BASE_URL + "/refresh-token")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "user/refresh_token",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰")
                ),
                responseFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰")
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            delete(this.BASE_URL + "/delete")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "user/delete",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk())
        ;
    }

    @Test
    @Transactional
    public void updateUser() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String testNickname = "test_nickname_" + System.currentTimeMillis();
        String testProfileUrl = "test_profile_url" + System.currentTimeMillis();

        String jsonRQ = """
            {
                "nickname" : "%s",
                "profileUrl" : "%s"
            }
        """.formatted(testNickname, testProfileUrl);

        this.mockMvc.perform(
            put(this.BASE_URL + "/update")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "user/update",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("변경할 닉네임"),
                    fieldWithPath("profileUrl").type(JsonFieldType.STRING).description("변경할 사진정보 URL")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void appSnsLogin() throws Exception{
        String jsonRQ = """
            {
                "id" : "TestNaverProviderId",
                "email" : "lkd9125@naver.com",
                "mobile" : "010-8370-3435",
                "name" : "이경도",
                "nickname" : "갱갱도",
                "platform" : "naver"
            }
        """;

        this.mockMvc.perform(
            post(this.BASE_URL + "/app/sns-login")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "user/app/sns-login",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("SNS 프로필 조회 후 나오는 플랫폼별 ProviderID"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("SNS 프로필 이메일, 첫 로그인시는 필수값"),
                    fieldWithPath("mobile").type(JsonFieldType.STRING).description("SNS 프로필 전화번호"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("SNS 프로필 이름"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("SNS 프로필 별명"),
                    fieldWithPath("platform").type(JsonFieldType.STRING).description("SNS 플랫폼명 [네이버 -> naver, 카카오 -> kakao]")
                ),
                responseFields(
                    fieldWithPath("token").type(JsonFieldType.OBJECT).description("인증객체"),
                    fieldWithPath("token.grantType").type(JsonFieldType.STRING).description("인증타입"),
                    fieldWithPath("token.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("token.refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰")
                )
            )
        )
        .andExpect(status().isOk());
    }


    @Test
    @Transactional
    public void register() throws Exception{

        String testId = "test_" + System.currentTimeMillis();

        String jsonRQ = """
            {
                "id" : "%s",
                "password" : "1234",
                "nickname" : "경경도",
                "name" : "이경도",
                "email" : "lkd9125@naver.com",
                "hpno" : "01083703435"
            }  
        """.formatted(testId);

        this.mockMvc.perform(
            post(this.BASE_URL + "/register")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "user/register",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("회원 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 별명"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("회원 이메일"),
                    fieldWithPath("hpno").type(JsonFieldType.STRING).description("회원 전화번호")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void login() throws Exception {

        this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "username" : "lkd9125",
                "password" : "1234"
            }  
        """;

        this.mockMvc.perform(
            post(this.BASE_URL + "/login")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "user/login",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING).description("회원 아이디"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("회원 비밀번호")
                ),
                responseFields(
                    fieldWithPath("token").type(JsonFieldType.OBJECT).description("인증객체"),
                    fieldWithPath("token.grantType").type(JsonFieldType.STRING).description("인증타입"),
                    fieldWithPath("token.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("token.refreshToken").type(JsonFieldType.STRING).description("리프레쉬 토큰")
                )
            )
        )
        .andExpect(status().isOk());
    }
}
