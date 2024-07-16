package com.safety.law.domain.user.model.notification;

import java.util.List;

import com.google.firebase.database.annotations.NotNull;
import com.safety.law.global.jpa.entity.NotificationAgreeEntity.NotificationType;

import lombok.Data;

@Data
public class NotificationAgreeRQ {

    @NotNull
    private String fcmDeviceToken;

    @NotNull
    private List<NotificationType> notificationType;

}
