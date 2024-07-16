package com.safety.law.api.feature.law;

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

import java.time.LocalDate;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ExceptionCollector;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.api.base.BaseTest;
import com.safety.law.global.jpa.entity.LawEntity;
import com.safety.law.global.jpa.repository.LawRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.model.TokenModel;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class LawTest extends BaseTest{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private LawRepository lawRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String BASE_URL = "/api/v1/law";

    @Test
    @Transactional
    public void changeLawList() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/change-list")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/change_list",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                queryParameters(
                    parameterWithName("pageNum").description("페이지 번호, Default 1").optional(),
                    parameterWithName("row").description("데이터 갯수, Default 5").optional(),
                    parameterWithName("startDate").description("검색 시작기간").optional(),
                    parameterWithName("endDate").description("검색 끝 기간").optional()
                    ),
                responseFields(
                    fieldWithPath("pageNum").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                    fieldWithPath("changeLawList").type(JsonFieldType.ARRAY).description("조회된 법률 리스트"),
                    fieldWithPath("changeLawList[].lawIdx").type(JsonFieldType.NUMBER).description("법률 고유키 번호").optional(),
                    fieldWithPath("changeLawList[].category").type(JsonFieldType.STRING).description("카테고리").optional(),
                    fieldWithPath("changeLawList[].title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("changeLawList[].changeDate").type(JsonFieldType.STRING).description("바뀐 날짜").optional()
                )
            )
        )
        .andExpect(status().isOk());

    }

    @Test
    @Transactional
    public void enhanceSearch() throws Exception{

        String jsonRQ = """
            {
                "keyWord" : "산업안전",
                "category" : 0,
                "pageNum" : 1,
                "row" : 1
            }
        """;
        
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            post(this.BASE_URL + "/enhance/search")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/enhance/search",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("keyWord").type(JsonFieldType.STRING).description("검색할 키워드 [제목, 내용"),
                    fieldWithPath("pageNum").type(JsonFieldType.NUMBER).description("페이지 번호, Default 1"),
                    fieldWithPath("row").type(JsonFieldType.NUMBER).description("데이터 갯수, Default 10"),
                    fieldWithPath("category").type(JsonFieldType.NUMBER).description("카테고리 번호, Default 전체조회")
                    ),
                responseFields(
                    fieldWithPath("searchDataList").type(JsonFieldType.ARRAY).description("조회된 법률 리스트"),
                    fieldWithPath("searchDataList[].lawIdx").type(JsonFieldType.NUMBER).description("법률 고유키 번호").optional(),
                    fieldWithPath("searchDataList[].lawDocId").type(JsonFieldType.STRING).description("문서번호").optional(),
                    fieldWithPath("searchDataList[].title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("searchDataList[].category").type(JsonFieldType.STRING).description("카테고리").optional()
                )
            )
        )
        .andExpect(status().isOk());

    }

    @Test
    public void getRankingKeyword() throws Exception{
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/ranking/keyword")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/ranking/keyword",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                responseFields(
                    fieldWithPath("scoreList").type(JsonFieldType.ARRAY).description("키워드 랭킹 리스트"),
                    fieldWithPath("scoreList[].keyword").type(JsonFieldType.STRING).description("키워드").optional(),
                    fieldWithPath("scoreList[].score").type(JsonFieldType.NUMBER).description("점수").optional()
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    public void getLawHistory() throws Exception{
        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String pageNum = "1";
        String row = "5";

        this.mockMvc.perform(
            get(this.BASE_URL + "/history")
            .contextPath("/api")
            .param("pageNum", pageNum)
            .param("row", row)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/history",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                queryParameters(
                    parameterWithName("row").description("받을 데이터 갯수 [Default 5개]").optional(),
                    parameterWithName("pageNum").description("페이지 [Default 1]").optional()
                ),
                responseFields(
                    fieldWithPath("count").type(JsonFieldType.NUMBER).description("총 데이터 갯수"),
                    fieldWithPath("pageNum").type(JsonFieldType.NUMBER).description("현재 페이지"),
                    fieldWithPath("lawHistory").type(JsonFieldType.ARRAY).description("최근 조회한 법률 리스트"),
                    fieldWithPath("lawHistory[].lawIdx").type(JsonFieldType.NUMBER).description("법률 고유 번호").optional(),
                    fieldWithPath("lawHistory[].category").type(JsonFieldType.STRING).description("카테고리").optional(),
                    fieldWithPath("lawHistory[].title").type(JsonFieldType.STRING).description("법률 제목").optional()
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void createComment() throws Exception{
        LawEntity lawEntity = lawRepository.findById(1L)
            .orElse(LawEntity.builder()
                .category("1")
                .content("내용")
                .highlightContent("내용")
                .docId("TEST_001")
                .score(3.11231251)
                .title("법률 1조")
                .lastUpdateDt(LocalDate.now())
                .build()
            );
    
        lawEntity = lawRepository.save(lawEntity);

        Long lawIdx = lawEntity.getLawIdx();

        String jsonRQ = """
            {
                "content" : "테스트 댓글",
                "lawIdx" : %d
            }
        """.formatted(lawIdx);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            post(this.BASE_URL + "/comment")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/comment/create",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("lawIdx").type(JsonFieldType.NUMBER).description("법률 고유 아이디")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    public void searchCategory() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/category")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/category",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                responseFields(
                    fieldWithPath("categories").type(JsonFieldType.ARRAY).description("카테고리 리스트"),
                    fieldWithPath("categories[].categoryNumber").type(JsonFieldType.NUMBER).description("카테고리 번호"),
                    fieldWithPath("categories[].categoryDesc").type(JsonFieldType.STRING).description("카테고리 설명")
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void detailLaw() throws Exception{

        String lawIdx = "3694";

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get(this.BASE_URL + "/detail/{lawIdx}", lawIdx)
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/detail",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                pathParameters(
                    parameterWithName("lawIdx").description("법률 고유번호")
                ),  
                responseFields(
                    fieldWithPath("lawIdx").type(JsonFieldType.NUMBER).description("법률 고유키 번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                    fieldWithPath("hilightContent").type(JsonFieldType.STRING).description("법률 내용"),
                    fieldWithPath("docId").type(JsonFieldType.STRING).description("문서번호"),
                    fieldWithPath("category").type(JsonFieldType.STRING).description("카테고리 번호"),
                    fieldWithPath("categoryDesc").type(JsonFieldType.STRING).description("카테고리 설명"),
                    fieldWithPath("view").type(JsonFieldType.NUMBER).description("조회수"),
                    fieldWithPath("lastUpdateDt").type(JsonFieldType.STRING).description("최종 수정일"),
                    fieldWithPath("commentList").type(JsonFieldType.ARRAY).description("댓글 리스트").optional(),
                    fieldWithPath("commentList[].commentIdx").type(JsonFieldType.NUMBER).description("댓글 고유 번호").optional(),
                    fieldWithPath("commentList[].content").type(JsonFieldType.STRING).description("내용").optional(),
                    fieldWithPath("commentList[].username").type(JsonFieldType.STRING).description("댓글 작성자").optional(),
                    fieldWithPath("commentList[].nickname").type(JsonFieldType.STRING).description("댓글 작성자 닉네임").optional(),
                    fieldWithPath("commentList[].lastUpdateDt").type(JsonFieldType.STRING).description("댓글 작성일 [최종수정일]").optional()
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    public void searchLawCount() throws Exception{
        String keyword = " ";

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/search/count")
            .contextPath("/api")
            .param("keyword", keyword)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/search/count",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                queryParameters(
                    parameterWithName("keyword").description("검색할 키워드 [제목, 내용 한번에 검색]")
                ),
                responseFields(
                    fieldWithPath("categoryCounts").type(JsonFieldType.ARRAY).description("카테고리별 키워드로 조회되는 데이터 건수"),
                    fieldWithPath("categoryCounts[].category").type(JsonFieldType.STRING).description("카테고리").optional(),
                    fieldWithPath("categoryCounts[].categoryDesc").type(JsonFieldType.STRING).description("카테고리 설명").optional(),
                    fieldWithPath("categoryCounts[].count").type(JsonFieldType.NUMBER).description("데이터 건수").optional()
                )
            )
        )
        .andExpect(status().isOk());


    }
    
    @Test
    @Transactional
    public void searchLaw() throws Exception{
    
        String pageNum = "1";
        String keyWord = " ";
        String row = "1";
        String category = "1";

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        this.mockMvc.perform(
            get(this.BASE_URL + "/search")
            .contextPath("/api")
            .param("pageNum", pageNum)
            .param("keyWord", keyWord)
            .param("row", row)
            .param("category", category)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "law/search",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                queryParameters(
                    parameterWithName("pageNum").description("페이지 번호, Default 1").optional(),
                    parameterWithName("keyWord").description("검색할 키워드 [제목, 내용 한번에 검색]"),
                    parameterWithName("row").description("데이터 갯수, Default 10").optional(),
                    parameterWithName("category").description("카테고리 번호, Default 전체조회").optional()
                ),
                responseFields(
                    fieldWithPath("searchDataList").type(JsonFieldType.ARRAY).description("법률 리스트"),
                    fieldWithPath("searchDataList[].lawIdx").type(JsonFieldType.NUMBER).description("법률 고유키 번호").optional(),
                    fieldWithPath("searchDataList[].lawDocId").type(JsonFieldType.STRING).description("문서번호").optional(),
                    fieldWithPath("searchDataList[].title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("searchDataList[].category").type(JsonFieldType.STRING).description("법률 카테고리").optional()
                )
            )
        )
        .andExpect(status().isOk());
    }
}
