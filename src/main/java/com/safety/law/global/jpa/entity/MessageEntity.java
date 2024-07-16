package com.safety.law.global.jpa.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.safety.law.global.base.DocsEnumType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "MESSAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_IDX")
    private Long messageIdx;

    @Column(name = "MESSAGE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "CERT_NUM", nullable = true)
    private String certNum;

    @Column(name = "PHONE_NUM", nullable = false)
    private String phoneNum;

    @Column(name = "CERT_YN", nullable = true)
    private Boolean certYn;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }

    @RequiredArgsConstructor
    public static enum MessageType implements DocsEnumType{
        LINK_CERT("계정연동 문자발송"), // 계정 연동
        UPDATE_CERT("프로필 업데이트 문자발송"), // 프로필 업데이트
        GUIDE("단순 안내 문자발송"), // 단순 안내
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
