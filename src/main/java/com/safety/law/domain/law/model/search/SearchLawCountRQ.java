package com.safety.law.domain.law.model.search;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SearchLawCountRQ {

    @NotNull
    private String keyword;

}
