package com.safety.law.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.domain.user.model.mypage.SaveBoardRS;
import com.safety.law.domain.user.model.mypage.SaveBoardRS.BoardModel;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.repository.BoardHeartReferenceQueryRepository;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.security.Principal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MypageService {

    private final UsersRepository usersRepository;

    private final BoardHeartReferenceQueryRepository boardHeartReferenceQueryRepository;
    
    /**
     * 저장된 게시물 조회
     * @return
     */
    @Transactional
    public SaveBoardRS getSaveBoard() {

        String username = Principal.getUser();

        usersRepository.findById(username)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        List<BoardModel> boardList = boardHeartReferenceQueryRepository.findByUsername(username);

        SaveBoardRS result = new SaveBoardRS();
        result.setBoardList(boardList);

        return result;
    }

}
