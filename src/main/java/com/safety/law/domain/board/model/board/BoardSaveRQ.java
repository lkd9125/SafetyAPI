package com.safety.law.domain.board.model.board;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoardSaveRQ {

    @NotNull
    private Long idx; // BOARD IDX

}
