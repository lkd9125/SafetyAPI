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

@Data
@Entity
@Table(name = "LAW_UPDATE_HISTORY")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LawUpdateHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LAW_UPDATE_HISTORY_IDX")
    private Long lawUpdateHistoryIdx;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "LAW_IDX", referencedColumnName = "LAW_IDX", nullable = false)
    private LawEntity lawEntity;

    @Column(name = "OLD_CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String oldContent;

    @Column(name = "NEW_CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String newContent;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
    }


}
