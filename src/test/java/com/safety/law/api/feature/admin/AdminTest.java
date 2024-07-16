package com.safety.law.api.feature.admin;

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
import com.safety.law.global.jpa.repository.UsersRepository;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class AdminTest extends BaseTest{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String BASE_URL = "/api/v1/admin";

    
    @Test
    @Transactional
    public void adminLogin() throws Exception {

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
                "admin/login",
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
