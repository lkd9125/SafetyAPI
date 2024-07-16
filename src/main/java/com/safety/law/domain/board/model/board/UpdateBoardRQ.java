package com.safety.law.domain.board.model.board;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBoardRQ {

    @NotNull
    private Long idx;

    @Length(max = 300)
    private String title;

    private String content;
}
