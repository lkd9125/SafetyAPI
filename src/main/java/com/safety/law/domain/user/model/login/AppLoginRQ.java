package com.safety.law.domain.user.model.login;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppLoginRQ {

    @NotNull
    @NotEmpty
    private String id;

    private String email;

    private String mobile;

    private String name;

    private String nickname;

    @NotNull
    @NotEmpty
    private String platform;

}
