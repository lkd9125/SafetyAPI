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
import lombok.ToString;

@Entity
@Table(name = "BOARD_COMMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"boardEntity"})
public class BoardCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_COMMENT_IDX", nullable = false)
    private Long boardCommentIdx;

    @Column(name = "CONTENT", nullable = false)
    private String content;
    
    @Column(name = "PARENT_IDX", nullable = true)
    private Long parentIdx;

    @Column(name = "CREATE_DT", nullable = true)
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = true)
    private LocalDateTime updateDt;

    @Column(name = "CREATE_USER", nullable = false)
    private String createUser;

    @Column(name = "UPDATE_USER", nullable = true)
    private String updateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_IDX", referencedColumnName = "BOARD_IDX", nullable = false)
    private BoardEntity boardEntity;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }

}
