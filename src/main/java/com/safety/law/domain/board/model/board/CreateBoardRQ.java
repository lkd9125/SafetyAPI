package com.safety.law.domain.board.model.board;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateBoardRQ {

    @NotEmpty
    @Length(max = 300)
    private String title;

    @NotEmpty
    private String content;
}
