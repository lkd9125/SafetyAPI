package com.safety.law.api.feature.common;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.safety.law.api.base.BaseTest;
import com.safety.law.global.jpa.repository.LawRepository;
import com.safety.law.global.jpa.repository.UsersRepository;


@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class CommonTest extends BaseTest{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private LawRepository lawRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String BASE_URL = "/api/v1/common";

    private final String GRANT_TYPE = "Bearer ";

    @Test
    public void messageSend() throws Exception{

        String jsonRQ = """
            {
                "phoneNum" : "01083703435",
                "type" : "LINK_CERT"
            }
        """;

        this.mockMvc.perform(
            post(this.BASE_URL + "/message/send")
            .contextPath("/api")
            .content(jsonRQ)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andDo(print())
        .andDo(
            document(
                "common/message/send",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("phoneNum").type(JsonFieldType.STRING).description("인증번호 전송할 휴대폰 번호"),
                    fieldWithPath("type").type(JsonFieldType.STRING).description("전송 타입 [LINK_CERT: 계정연동용 인증문자, UPDATE_CERT: 업데이트 인증문자, GUIDE: 안내]")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }


}
