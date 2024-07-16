package com.safety.law.domain.board.model.board;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBoardCommentRQ {

    @NotNull
    private Long boardIdx;

    private Long parentIdx;

    @NotEmpty
    private String content;

}
