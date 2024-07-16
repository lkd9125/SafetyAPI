package com.safety.law.domain.law.model.comment;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRQ {

    @NotBlank
    @Length(max = 1000)
    private String content;

    @NotNull
    @Min(1)
    private Long lawIdx;

}
