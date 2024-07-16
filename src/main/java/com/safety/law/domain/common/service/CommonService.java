package com.safety.law.domain.common.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safety.law.domain.common.model.MessageTemplate;
import com.safety.law.domain.common.model.message.SendMessageRQ;
import com.safety.law.global.jpa.entity.MessageEntity;
import com.safety.law.global.jpa.entity.MessageEntity.MessageEntityBuilder;
import com.safety.law.global.jpa.repository.MessageRepository;
import com.safety.law.global.util.RandomUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonService {

    private final MessageRepository messageRepository;
    
    @Transactional
    public Boolean certMessageSend(SendMessageRQ rq) {
        MessageTemplate template = new MessageTemplate();

        int[] randomArray = null;
        String certNum = null;

        MessageEntityBuilder builder = MessageEntity.builder();
        builder.type(rq.getType());
        builder.phoneNum(rq.getPhoneNum().replace("-", ""));

        switch (rq.getType()) {
            case LINK_CERT:
                randomArray = RandomUtil.randomAllowDuplicationIntegerArray(9,6);
                certNum = Arrays.toString(randomArray).replaceAll("[^0-9]", "");

                builder.certNum(certNum);
                builder.phoneNum(rq.getPhoneNum().replace("-", ""));
                builder.content(template.certMessageTemplate(certNum));
                builder.certYn(false);

                break;
            case UPDATE_CERT:
                randomArray = RandomUtil.randomAllowDuplicationIntegerArray(9,6);
                certNum = Arrays.toString(randomArray).replaceAll("[^0-9]", "");

                builder.certNum(certNum);
                builder.phoneNum(rq.getPhoneNum().replace("-", ""));
                builder.content(template.certMessageTemplate(certNum));
                builder.certYn(false);

                break;  
            case GUIDE:

                builder.phoneNum(rq.getPhoneNum().replace("-", ""));
                builder.content(template.guideTemplate());

                break;
        }
        MessageEntity messageEntity =  builder.build();

        messageRepository.save(messageEntity);

        // TODO:: 문자메세지 보내는 기능
        boolean sendMessage = true;

        log.warn("MessageContent => {}", messageEntity.getContent());

        return sendMessage;
    }

}
