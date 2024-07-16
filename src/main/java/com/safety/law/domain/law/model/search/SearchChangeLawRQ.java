package com.safety.law.domain.law.model.search;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SearchChangeLawRQ {

    private LocalDate startDate;
    
    private LocalDate endDate;

    @Min(0)
    private Integer pageNum = 1; // 페이지 숫자 [ 기본 1 ]
    
    private Integer row = 5; // 조회할 데이터 숫자 [ 기본 5 ]

}
