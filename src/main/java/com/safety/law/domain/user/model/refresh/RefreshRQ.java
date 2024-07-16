package com.safety.law.domain.user.model.refresh;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshRQ {

    @NotNull
    private String refreshToken;

}
