package com.safety.law.domain.common.model.message;

import com.safety.law.global.jpa.entity.MessageEntity.MessageType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRQ {

    @NotNull
    private String phoneNum;

    @NotNull
    private MessageType type;    

}
