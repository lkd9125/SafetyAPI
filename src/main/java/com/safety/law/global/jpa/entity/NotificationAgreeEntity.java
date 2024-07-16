package com.safety.law.global.jpa.entity;

import java.time.LocalDateTime;

import com.safety.law.global.base.DocsEnumType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "NOTIFICATION_AGREE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationAgreeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_AGREE_IDX")
    private Long pushNotificationAgreeIdx;

    @Column(name = "NOTIFICATION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "DELETE_DT", nullable = true)
    private LocalDateTime deleteDt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTIFICATION_USERNAME", referencedColumnName = "USERNAME", nullable = false)
    private UsersEntity usersEntity;


    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }

    @RequiredArgsConstructor
    public static enum NotificationType implements DocsEnumType{
        
        QNA_NOTIFICATION("QnA게시물 관련 알림발송"), // QnA 답글 받았을 시 보내는 알림
        ;

        private final String desc;
        
        
        @Override
        public String getType() {
            return this.name();
        }

        @Override
        public String getDescription() {
            return this.desc;
        } 
        
        

    }

}   
