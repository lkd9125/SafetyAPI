package com.safety.law.domain.board.service;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.domain.board.model.board.BoardSaveRQ;
import com.safety.law.domain.board.model.board.CommentBoardRS;
import com.safety.law.domain.board.model.board.CreateBoardCommentRQ;
import com.safety.law.domain.board.model.board.CreateBoardRQ;
import com.safety.law.domain.board.model.board.SearchBoardRQ;
import com.safety.law.domain.board.model.board.SearchBoardRS;
import com.safety.law.domain.board.model.board.SearchBoardRS.BoardModel;
import com.safety.law.domain.board.model.fcm.FcmBoardParameter;
import com.safety.law.domain.board.model.board.UpdateBoardCommentRQ;
import com.safety.law.domain.board.model.board.UpdateBoardRQ;
import com.safety.law.domain.board.model.board.CommentBoardRS.CommentModel;
import com.safety.law.global.common.model.FcmSendModel;
import com.safety.law.global.common.model.FcmParameter.FcmScreen;
import com.safety.law.global.common.service.CommonFcmService;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.BoardCommentEntity;
import com.safety.law.global.jpa.entity.BoardEntity;
import com.safety.law.global.jpa.entity.BoardHeartReferenceEntity;
import com.safety.law.global.jpa.entity.UsersDtlEntity;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity.NotificationType;
import com.safety.law.global.jpa.repository.BoardCommentQueryRepository;
import com.safety.law.global.jpa.repository.BoardCommentRepository;
import com.safety.law.global.jpa.repository.BoardHeartReferenceQueryRepository;
import com.safety.law.global.jpa.repository.BoardHeartReferenceRepository;
import com.safety.law.global.jpa.repository.BoardQueryRepository;
import com.safety.law.global.jpa.repository.BoardRepository;
import com.safety.law.global.jpa.repository.UsersDtlRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.Principal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final CommonFcmService commonFcmService;

    private final UsersRepository usersRepository;

    private final UsersDtlRepository usersDtlRepository;
    
    private final BoardRepository boardRepository;

    private final BoardQueryRepository boardQueryRepository;

    private final BoardCommentRepository boardCommentRepository;

    private final BoardCommentQueryRepository boardCommentQueryRepository;

    private final BoardHeartReferenceRepository boardHeartReferenceRepository;

    private final BoardHeartReferenceQueryRepository boardHeartReferenceQueryRepository;
    
    /**
     * 게시글 추가
     * @param rq
     * @return
     */
    @Transactional
    public Boolean createBoard(CreateBoardRQ rq) {

        String username = Principal.getUser();
        
        BoardEntity boardEntity = BoardEntity.builder()
            .title(rq.getTitle())
            .content(rq.getContent())
            .createUser(username)
            .updateUser(username)
            .build();

        boardRepository.save(boardEntity);

        return true;
    }

    /**
     * 게시글 조회
     * @param rq
     * @return
     */
    @Transactional(readOnly = true)
    public SearchBoardRS getBoardList(@Valid SearchBoardRQ rq) {

        String username = Principal.getUser();
        
        List<BoardModel> boardList = boardQueryRepository.getBoardList(rq, username);

        SearchBoardRS result = new SearchBoardRS();
        result.setBoardList(boardList);

        return result;
    }

    /**
     * 댓글추가
     * @param rq
     * @return
     */
    @Transactional
    public Boolean commentCreate(@Valid CreateBoardCommentRQ rq) {

        BoardEntity boardEntity = boardRepository.findById(rq.getBoardIdx())
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        String username = Principal.getUser();

        if(rq.getParentIdx() != null){
            BoardCommentEntity parentCommentEntity = boardCommentRepository.findById(rq.getParentIdx())
                .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

            if(boardEntity.getBoardIdx().longValue() != parentCommentEntity.getBoardEntity().getBoardIdx().longValue()) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
        }

        BoardCommentEntity boardCommentEntity = BoardCommentEntity.builder()
            .content(rq.getContent())
            .parentIdx(rq.getParentIdx() != null ? rq.getParentIdx() : null)
            .createUser(username)
            .updateUser(username)
            .boardEntity(boardEntity)
            .build();

        boardCommentRepository.save(boardCommentEntity);

        UsersDtlEntity users = usersDtlRepository.findById(username).orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        // FCM 푸쉬알림 코드
        String content;

        if(rq.getContent().length() < 10){
            content = rq.getContent();
        } else {
            content = rq.getContent().substring(0, 10) + "...";
        }

        FcmSendModel fcmSendModel = commonFcmService.getFcmSendModel(NotificationType.QNA_NOTIFICATION);
        fcmSendModel.setTitle(fcmSendModel.getTitle().formatted(users.getNickname()));
        fcmSendModel.setContent(content);

        FcmBoardParameter param = new FcmBoardParameter();
        param.setId(String.valueOf(boardEntity.getBoardIdx()));
        param.setScreen(FcmScreen.BOARD_DETAIL.name());
        
        String targetUsername = boardEntity.getCreateUser();

        try {
            commonFcmService.fcmNotificationSend(targetUsername, fcmSendModel, param);
        } catch (IOException e) {
            log.error("", e);
        } catch (AppException e){
            ExceptionCode code = e.getErrorCode();

            if(code == ExceptionCode.NULL_FCM_TOKEN){
                log.warn("FCM TOKEN IS NULL");
            } else {
                throw e;
            }
        } catch (Exception e){
            log.error("", e);
        }

        return true;
    }

    /**
     * 댓글 조회
     * @param idx BoardIDX
     * @return
     */
    public CommentBoardRS getComment(Long idx) {

        List<CommentModel> commentList = this.sortComments(boardCommentQueryRepository.getComment(idx));

        CommentBoardRS result = new CommentBoardRS();
        result.setCommentList(commentList);

        return result;
    }

    private List<CommentModel> sortComments(List<CommentModel> comments) {
        // 결과를 담을 리스트
        List<CommentModel> sortedComments = new ArrayList<>();

        // CommentIdx를 키로, CommentModel을 값으로 갖는 맵을 생성합니다.
        Map<Long, CommentModel> commentMap = new HashMap<>();
        for (CommentModel comment : comments) {
            commentMap.put(comment.getCommentIdx(), comment);
        }

        // 부모 댓글을 먼저 추가합니다.
        for (CommentModel comment : comments) {
            if (comment.getParentIdx() == null) {
                sortedComments.add(comment);
                addChildren(comment, sortedComments, commentMap);
            }
        }

        return sortedComments;
    }

    private void addChildren(CommentModel parent, List<CommentModel> sortedComments, Map<Long, CommentModel> commentMap) {
        // 각 부모 댓글에 대해 자식 댓글을 찾습니다.
        for (CommentModel comment : commentMap.values()) {
            if (parent.getCommentIdx().equals(comment.getParentIdx())) {
                sortedComments.add(comment);
                // 재귀적으로 자식 댓글을 찾습니다.
                addChildren(comment, sortedComments, commentMap);
            }
        }
    }

    /**
     * 게시물 데이터 삭제
     * @param idx
     * @return
     */
    @Transactional
    public Boolean deleteBoard(Long idx) {

        BoardEntity boardEntity = boardRepository.findById(idx).orElse(null);

        if(boardEntity == null) return true;

        String username = Principal.getUser();
        String boardUser = boardEntity.getCreateUser();

        if(!username.equals(boardUser)) throw new AppException(ExceptionCode.OTHRE_USER_DATA);

        boardRepository.deleteById(idx);

        return true;
    }

    /**
     * 게시물 수정
     * @param rq
     * @return
     */
    @Transactional
    public Boolean updateBoard(@Valid UpdateBoardRQ rq) {
        BoardEntity boardEntity = boardRepository.findById(rq.getIdx())
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        String username = Principal.getUser();
        String boardUser = boardEntity.getCreateUser();

        if(!username.equals(boardUser)) throw new AppException(ExceptionCode.OTHRE_USER_DATA);

        if(rq.getTitle() != null && rq.getTitle().length() > 0) boardEntity.setTitle(rq.getTitle());
        if(rq.getContent() != null && rq.getContent().length() > 0) boardEntity.setContent(rq.getContent());

        return true;
    }

    /**
     * 댓글 삭제
     * @param idx 댓글 IDX
     * @return
     */
    @Transactional
    public Boolean deleteComment(Long idx) {
        BoardCommentEntity boardCommentEntity = boardCommentRepository.findById(idx).orElse(null);

        if(boardCommentEntity == null) return true;

        String username = Principal.getUser();
        String boardUser = boardCommentEntity.getCreateUser();

        if(!username.equals(boardUser)) throw new AppException(ExceptionCode.OTHRE_USER_DATA);

        boardCommentRepository.deleteById(idx);
        
        return true;
    }

    /**
     * 댓글수정
     * @param rq
     * @return
     */
    @Transactional
    public Boolean updateComment(@Valid UpdateBoardCommentRQ rq) {
        BoardCommentEntity boardCommentEntity = boardCommentRepository.findById(rq.getIdx())
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        String username = Principal.getUser();
        String boardUser = boardCommentEntity.getCreateUser();

        if(!username.equals(boardUser)) throw new AppException(ExceptionCode.OTHRE_USER_DATA);

        if(rq.getContent() != null && rq.getContent().length() > 0) boardCommentEntity.setContent(rq.getContent());

        return true;
    }

    /**
     * 게시물 저장
     * @param rq
     * @return
     */
    @Transactional
    public Boolean boardSave(@Valid BoardSaveRQ rq) {

        String username = Principal.getUser();

        UsersEntity usersEntity = usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));
        
        BoardEntity boardEntity = boardRepository.findById(rq.getIdx())
            .orElseThrow(() -> new AppException(ExceptionCode.DATA_NOT_FIND));

        
        BoardHeartReferenceEntity validEntity = boardHeartReferenceQueryRepository.boardSaveValid(username, rq.getIdx());

        if(validEntity == null){
            BoardHeartReferenceEntity boardHeartReferenceEntity = BoardHeartReferenceEntity.builder()
            .usersEntity(usersEntity)
            .boardEntity(boardEntity)
            .build();

            boardHeartReferenceRepository.save(boardHeartReferenceEntity);
        } else {
            Long idx = validEntity.getBoardHeartRefrenceIdx();

            boardHeartReferenceRepository.deleteById(idx);
        }

        return true;
    }

    public BoardModel boardDetail(Long idx) {

        String username = Principal.getUser();

        return boardQueryRepository.getBoardDetail(idx, username);
    }

}
