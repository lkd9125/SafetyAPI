package com.safety.law.global.jpa.entity;

import java.util.List;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "BOARD")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_IDX", nullable = false)
    private Long boardIdx;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    private LocalDateTime updateDt;

    @Column(name = "CREATE_USER", nullable = false)
    private String createUser;

    @Column(name = "UPDATE_USER", nullable = true)
    private String updateUser;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "boardEntity")
    private List<BoardCommentEntity> boardCommentList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "boardEntity")
    private List<BoardHeartReferenceEntity> boardHeartReferenceCommentList;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }

}
