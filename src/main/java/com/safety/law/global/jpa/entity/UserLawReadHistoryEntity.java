package com.safety.law.global.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_LAW_READ_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLawReadHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_LAW_READ_HISTORY_IDX")
    private Long userLawReadHistoryIdx;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "LAW_IDX")
    private Long lawIdx;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }
}
