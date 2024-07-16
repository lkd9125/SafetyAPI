package com.safety.law.domain.board.model.board;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateBoardCommentRQ {

    @NotNull
    private Long idx;

    private String content;
    
}
