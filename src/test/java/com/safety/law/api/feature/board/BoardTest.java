package com.safety.law.api.feature.board;

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

import java.util.Arrays;

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
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.api.base.BaseTest;
import com.safety.law.global.jpa.entity.BoardCommentEntity;
import com.safety.law.global.jpa.entity.BoardEntity;
import com.safety.law.global.jpa.repository.BoardCommentRepository;
import com.safety.law.global.jpa.repository.BoardRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.model.TokenModel;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class BoardTest extends BaseTest {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardCommentRepository boardCommentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String BASE_URL = "/api/v1/board";

    @Test
    @Transactional
    public void boardSaveAndDelete() throws Exception{
        BoardEntity boardEntity = BoardEntity
            .builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "idx" : %s
            }        
        """.formatted(boardEntity.getBoardIdx());

        this.mockMvc.perform(
            post(this.BASE_URL + "/save-delete")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/save_delete",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("idx").type(JsonFieldType.NUMBER).description("저장[삭제]할 게시물 고유키 번호")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception{

        BoardEntity boardEntity = BoardEntity.builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        BoardCommentEntity boardCommentEntity = BoardCommentEntity
            .builder()
            .content("테스트 댓글")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .boardEntity(boardEntity)
            .build();

        boardCommentRepository.save(boardCommentEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "idx" : 5,
                "content" : "수정된댓글"
            }
        """.formatted(boardCommentEntity.getBoardCommentIdx());

        this.mockMvc.perform(
            put(this.BASE_URL + "/comment/update")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/comment/update",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("idx").type(JsonFieldType.NUMBER).description("댓글 고유키 번호"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 내용")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception{
        BoardEntity boardEntity = BoardEntity.builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        BoardCommentEntity boardCommentEntity = BoardCommentEntity
            .builder()
            .content("테스트 댓글")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .boardEntity(boardEntity)
            .build();

        boardCommentRepository.save(boardCommentEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String boardCommentIdx = String.valueOf(boardCommentEntity.getBoardCommentIdx().longValue());

        this.mockMvc.perform(
            RestDocumentationRequestBuilders.delete(this.BASE_URL + "/comment/delete/{boardCommentIdx}", boardCommentIdx)
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/comment/delete",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                pathParameters(
                    parameterWithName("boardCommentIdx").description("댓글 고유키 번호")
                ),  
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void getComment() throws Exception{

        BoardEntity boardEntity = BoardEntity.builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        BoardCommentEntity boardCommentEntity = BoardCommentEntity
            .builder()
            .content("테스트 댓글")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .boardEntity(boardEntity)
            .build();

        boardEntity.setBoardCommentList(Arrays.asList(boardCommentEntity));

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String boardIdx = String.valueOf(boardEntity.getBoardIdx().longValue());

        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get(this.BASE_URL + "/comment/{boardIdx}", boardIdx)
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/comment/search",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                pathParameters(
                    parameterWithName("boardIdx").description("게시물 고유키 번호")
                ),  
                responseFields(
                    fieldWithPath("commentList").type(JsonFieldType.ARRAY).description("댓글 리스트"),
                    fieldWithPath("commentList[].commentIdx").type(JsonFieldType.NUMBER).description("댓글 고유키 번호").optional(),
                    fieldWithPath("commentList[].parentIdx").type(JsonFieldType.NUMBER).description("[대댓글]상위 댓글 고유키 번호").optional(),
                    fieldWithPath("commentList[].content").type(JsonFieldType.STRING).description("내용").optional(),
                    fieldWithPath("commentList[].createDt").type(JsonFieldType.STRING).description("생성일시").optional(),
                    fieldWithPath("commentList[].updateDt").type(JsonFieldType.STRING).description("수정일시").optional(),
                    fieldWithPath("commentList[].createUser").type(JsonFieldType.STRING).description("생성유저").optional(),
                    fieldWithPath("commentList[].createUserName").type(JsonFieldType.STRING).description("생성유저이름").optional(),
                    fieldWithPath("commentList[].createUserProfileUrl").type(JsonFieldType.STRING).description("댓글 유저 프로필이미지").optional()
                )
            )
        )
        .andExpect(status().isOk());
    }


    @Test
    @Transactional
    public void createComment() throws Exception{

        BoardEntity boardEntity = BoardEntity
            .builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "boardIdx" : %s,
                "content" : "게시물의 댓글"
            }        
        """.formatted(boardEntity.getBoardIdx());

        this.mockMvc.perform(
            post(this.BASE_URL + "/comment/create")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/comment/create",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("boardIdx").type(JsonFieldType.NUMBER).description("게시물 고유키 번호"),
                    fieldWithPath("parentIdx").type(JsonFieldType.STRING).description("대댓글 시 사용, 상위 댓글의 고유키 번호").optional(),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());

    }

    @Test
    @Transactional
    public void updateBoard() throws Exception{

        BoardEntity boardEntity = BoardEntity
            .builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "idx" : %s,
                "title" : "수정된 제목입니다",
                "content" : ""
            }        
        """.formatted(boardEntity.getBoardIdx());

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
                "board/update",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("idx").type(JsonFieldType.NUMBER).description("게시물 고유키 번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("수정할 제목 [300자 이하]"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("수정할 내용")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void deleteBoard() throws Exception{
        
        BoardEntity boardEntity = BoardEntity
            .builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String boardIdx = String.valueOf(boardEntity.getBoardIdx().longValue());

        this.mockMvc.perform(
            RestDocumentationRequestBuilders.delete(this.BASE_URL + "/delete/{boardIdx}", boardIdx)
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/delete",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                pathParameters(
                    parameterWithName("boardIdx").description("게시물 고유키 번호")
                ),  
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void boardDetail() throws Exception{
        BoardEntity boardEntity = BoardEntity
            .builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .createUser("lkd9125")
            .updateUser("lkd9125")
            .build();

        boardEntity = boardRepository.save(boardEntity);

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        Long idx = boardEntity.getBoardIdx();

        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get(this.BASE_URL + "/{idx}", idx)
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/detail",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                pathParameters(
                    parameterWithName("idx").description("게시물 고유키 번호")
                ),  
                responseFields(
                    fieldWithPath("boardIdx").type(JsonFieldType.NUMBER).description("게시물 고유키 번호").optional(),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("내용").optional(),
                    fieldWithPath("heartCount").type(JsonFieldType.NUMBER).description("좋아요 숫자").optional(),
                    fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("댓글 갯수").optional(),
                    fieldWithPath("selfHeartValid").type(JsonFieldType.BOOLEAN).description("자신이 좋아요를 했는지 여부[TRUE : 자신이 저장함, FALSE : 자신이 저장 안함]").optional(),
                    fieldWithPath("createDt").type(JsonFieldType.STRING).description("생성일시").optional(),
                    fieldWithPath("updateDt").type(JsonFieldType.STRING).description("수정일시").optional(),
                    fieldWithPath("createUser").type(JsonFieldType.STRING).description("생성한 유저").optional(),
                    fieldWithPath("createUserName").type(JsonFieldType.STRING).description("생성한 유저 이름").optional(),
                    fieldWithPath("createUserProfileUrl").type(JsonFieldType.STRING).description("생성한 유저 프로필사진").optional()
                )
            )
        )
        .andExpect(status().isOk());

    }

    @Test
    public void getBoardList() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String keyWord = "";
        String pageNum = "1";
        String row = "10";

        this.mockMvc.perform(
            get(this.BASE_URL + "/list")
            .contextPath("/api")
            .param("pageNum", pageNum)
            .param("keyWord", keyWord)
            .param("row", row)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/list",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                queryParameters(
                    parameterWithName("pageNum").description("페이지 번호, Default 1").optional(),
                    parameterWithName("keyWord").description("검색할 키워드 Default 전체검색").optional(),
                    parameterWithName("row").description("데이터 갯수, Default 10").optional()
                ),
                responseFields(
                    fieldWithPath("boardList").type(JsonFieldType.ARRAY).description("게시물 리스트"),
                    fieldWithPath("boardList[].boardIdx").type(JsonFieldType.NUMBER).description("게시물 고유키 번호").optional(),
                    fieldWithPath("boardList[].title").type(JsonFieldType.STRING).description("제목").optional(),
                    fieldWithPath("boardList[].content").type(JsonFieldType.STRING).description("내용").optional(),
                    fieldWithPath("boardList[].heartCount").type(JsonFieldType.NUMBER).description("좋아요 숫자").optional(),
                    fieldWithPath("boardList[].commentCount").type(JsonFieldType.NUMBER).description("댓글 갯수").optional(),
                    fieldWithPath("boardList[].selfHeartValid").type(JsonFieldType.BOOLEAN).description("자신이 좋아요를 했는지 여부[TRUE : 자신이 저장함, FALSE : 자신이 저장 안함]").optional(),
                    fieldWithPath("boardList[].createDt").type(JsonFieldType.STRING).description("생성일시").optional(),
                    fieldWithPath("boardList[].updateDt").type(JsonFieldType.STRING).description("수정일시").optional(),
                    fieldWithPath("boardList[].createUser").type(JsonFieldType.STRING).description("생성한 유저").optional(),
                    fieldWithPath("boardList[].createUserName").type(JsonFieldType.STRING).description("생성한 유저 이름").optional(),
                    fieldWithPath("boardList[].createUserProfileUrl").type(JsonFieldType.STRING).description("생성한 유저 프로필 이미지").optional()
                )
            )
        )
        .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void boardCreate() throws Exception{

        TokenModel tokenModel = this.getAdminToken(usersRepository, passwordEncoder);

        String jsonRQ = """
            {
                "title": "게시물 1번입니다",
                "content" : "내용내용 ㅋㅋ"
            }        
        """;

        this.mockMvc.perform(
            post(this.BASE_URL + "/create")
            .contextPath("/api")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(jsonRQ)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", tokenModel.getGrantType() + " " + tokenModel.getAccessToken())
        )
        .andDo(print())
        .andDo(
            document(
                "board/create",
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("Authorization").description("Access토큰 [로그인 발급]")
                ),
                requestFields(
                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시물 제목 [300자 이하]"),
                    fieldWithPath("content").type(JsonFieldType.STRING).description("게시물 내용")
                ),
                responseBody()
            )
        )
        .andExpect(status().isOk());
    }

}
