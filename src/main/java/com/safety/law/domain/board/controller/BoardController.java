package com.safety.law.domain.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safety.law.domain.board.model.board.BoardSaveRQ;
import com.safety.law.domain.board.model.board.CommentBoardRS;
import com.safety.law.domain.board.model.board.CreateBoardCommentRQ;
import com.safety.law.domain.board.model.board.CreateBoardRQ;
import com.safety.law.domain.board.model.board.SearchBoardRQ;
import com.safety.law.domain.board.model.board.SearchBoardRS;
import com.safety.law.domain.board.model.board.SearchBoardRS.BoardModel;
import com.safety.law.domain.board.model.board.UpdateBoardCommentRQ;
import com.safety.law.domain.board.model.board.UpdateBoardRQ;
import com.safety.law.domain.board.service.BoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/board")
public class BoardController {

    private final BoardService BoardService;

    /**
     * 게시물 추가
     * @param rq
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Boolean> createBoard(@RequestBody @Valid CreateBoardRQ rq){ 

        Boolean result = BoardService.createBoard(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 게시물 조회
     * @param rq keyword: 타이틀 검색, pageNum: 페이지 번호, row: 데이터 갯수
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<SearchBoardRS> getBoardList(@Valid SearchBoardRQ rq){

        SearchBoardRS result = BoardService.getBoardList(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 게시물 단건 조회
     * @param rq keyword: 타이틀 검색, pageNum: 페이지 번호, row: 데이터 갯수
     * @return
     */
    @GetMapping("/{idx}")
    public ResponseEntity<BoardModel> boardDetail(@PathVariable(name = "idx") Long idx){

        BoardModel result = BoardService.boardDetail(idx);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 게시판 삭제
     * @param idx 게시판 번호
     * @return
     */
    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<Boolean> deleteBoard(@PathVariable(name = "idx") Long idx){

        Boolean result = BoardService.deleteBoard(idx);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 게시판 수정
     * @param rq
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateBoard(@RequestBody @Valid UpdateBoardRQ rq){

        Boolean result = BoardService.updateBoard(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 댓글 추가
     * @param rq
     * @return
     */
    @PostMapping("/comment/create")
    public ResponseEntity<Boolean> commentCreate(@RequestBody @Valid CreateBoardCommentRQ rq){

        Boolean result = BoardService.commentCreate(rq);

        return ResponseEntity.ok().body(result); 
    }

    /**
     * 댓글들 조회
     * @param idx 댓글 IDX
     * @return
     */
    @GetMapping("/comment/{idx}")
    public ResponseEntity<CommentBoardRS> getComment(@PathVariable(name = "idx") Long idx){
        log.warn("idx => {}", idx);
        
        CommentBoardRS result = BoardService.getComment(idx);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 댓글 삭제
     * @param idx 댓글 IDX
     * @return
     */
    @DeleteMapping("/comment/delete/{idx}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable(name = "idx") Long idx){

        Boolean result = BoardService.deleteComment(idx);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 댓글 수정
     * @param rq
     * @return
     */
    @PutMapping("/comment/update")
    public ResponseEntity<Boolean> updateComment(@RequestBody @Valid UpdateBoardCommentRQ rq){

        Boolean result = BoardService.updateComment(rq);

        return ResponseEntity.ok().body(result);
    }

    /**
     * 원하는 게시물 저장, 삭제 [마이페이지 확인용]
     * 기존 저장된 상태에서 다시 조회시 삭제됨
     * @param rq
     * @return
     */
    @PostMapping("/save-delete")
    public ResponseEntity<Boolean> boardSaveAndDelete(@RequestBody @Valid BoardSaveRQ rq){

        Boolean result = BoardService.boardSave(rq);

        return ResponseEntity.ok().body(result);
    }

    
}
