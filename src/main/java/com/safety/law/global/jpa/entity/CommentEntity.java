package com.safety.law.global.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "COMMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_IDX", nullable = false)
    private Long commentIdx;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATE_USER", nullable = false)
    private String createUser;

    @Column(name = "NICKNAME", nullable = false)
    private String nickname;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "UPDATE_USER", nullable = false)
    private String updateUser;

    @Column(name = "UPDATE_DT", nullable = false)
    private LocalDateTime updateDt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "LAW_IDX", referencedColumnName = "LAW_IDX", nullable = false)
    private LawEntity lawEntity;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }

}
