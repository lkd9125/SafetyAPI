package com.safety.law.global.common.service;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.safety.law.global.common.model.FcmParameter;
import com.safety.law.global.common.model.FcmSendModel;
import com.safety.law.global.exception.AppException;
import com.safety.law.global.exception.ExceptionCode;
import com.safety.law.global.jpa.entity.UsersEntity;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity.NotificationType;
import com.safety.law.global.jpa.repository.UsersRepository;
import com.safety.law.global.util.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonFcmService {

    private final UsersRepository usersRepository;

    private final FirebaseMessaging firebaseMessaging;

    public <P extends FcmParameter> Boolean fcmNotificationSend(String targetUsername, FcmSendModel fcmSendModel, P param) throws IOException{

        UsersEntity usersEntity = usersRepository.findById(targetUsername)
            .orElseThrow(() -> new AppException(ExceptionCode.NOT_FOUNT_USER));

        if(usersEntity.getFcmDeviceToken() == null) throw new AppException(ExceptionCode.NULL_FCM_TOKEN);

        Notification notification = Notification.builder()
            .setTitle(fcmSendModel.getTitle())
            .setBody(fcmSendModel.getContent())
            .build();

        Message.Builder messageBuilder = Message.builder()
            .setToken(usersEntity.getFcmDeviceToken())
            .setNotification(notification);

        if(param != null){
            for(Map.Entry<String, Object> entrySet : param.getParameter().entrySet()){

                if(entrySet.getKey().equals("log")) continue;

                String data;

                if(entrySet.getValue() instanceof String value){
                    data = value;
                } else {
                    data = JsonUtils.getInstance().writeValueAsString(entrySet.getValue());
                }

                messageBuilder.putData(entrySet.getKey(), data);
            }
        }
            
        Message message = messageBuilder.build();

        try {
            firebaseMessaging.send(message);
        } catch (Exception e) {
            log.error("", e);
        }

        return true;
    }

    public FcmSendModel getFcmSendModel(NotificationType type){
        FcmSendModel result = new FcmSendModel();

        switch (type) {
            case QNA_NOTIFICATION:
                result.setTitle("회원님의 게시물에 %s님의 답글이 달렸습니다.");
                result.setContent("%s");
                break;
            default:
            
            throw new AppException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
        
        return result;
    }



}
