package com.safety.law.global.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "BOARD_HEART_REFRENCE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"usersEntity", "boardEntity"})
public class BoardHeartReferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_HEART_REFRENCE_IDX", nullable = false)
    private Long boardHeartRefrenceIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_IDX", referencedColumnName = "BOARD_IDX", nullable = false)
    private BoardEntity boardEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_USERNAME", referencedColumnName = "USERNAME", nullable = false)
    private UsersEntity usersEntity;
}
