package com.safety.law.domain.board.model.board;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SearchBoardRQ {

    @Length(max = 300)
    private String keyWord = ""; // 검색할 키워드 TITLE, Content 둘다 조회
    
    @Min(0)
    private Integer pageNum = 1; // 페이지 숫자 [ 기본 1 ]
    
    private Integer row = 10; // 조회할 데이터 숫자 [ 기본 10 ]
}
