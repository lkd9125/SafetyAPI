package com.safety.law.api.feature.mypage;

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

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
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
import com.safety.law.global.security.model.TokenModel;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class MypageTest extends BaseTest{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private LawRepository lawRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String BASE_URL = "/api/v1/mypage";

    @Test
    public void getSaveBoard() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/save-board")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "mypage/save_board",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                responseFields(
                    fieldWithPath("boardList").type(JsonFieldType.ARRAY).description("게시물 리스트"),
                    fieldWithPath("boardList[].idx").type(JsonFieldType.NUMBER).description("게시물 고유키 번호").optional(),
                    fieldWithPath("boardList[].title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("boardList[].content").type(JsonFieldType.STRING).description("내용").optional(),
                    fieldWithPath("boardList[].createDt").type(JsonFieldType.STRING).description("생성일시").optional(),
                    fieldWithPath("boardList[].updateDt").type(JsonFieldType.STRING).description("수정일시").optional()
                )
            )
        )
        .andExpect(status().isOk());

    }

}
