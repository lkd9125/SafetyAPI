package com.safety.law.domain.user.model.cert;

import com.safety.law.global.jpa.entity.MessageEntity.MessageType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageCertRQ {

    @NotNull
    private String phoneNum;

    @NotNull
    private String certNum;

    @NotNull
    private MessageType type;
}
