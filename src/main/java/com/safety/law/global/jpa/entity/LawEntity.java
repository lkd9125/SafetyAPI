package com.safety.law.global.jpa.entity;

import java.util.List;
import java.time.LocalDate;

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
import lombok.ToString;

@Data
@Entity
@Table(name = "LAW")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "commentList")
public class LawEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LAW_IDX")
    private Long lawIdx;

    @Column(name = "CATEGORY", nullable = false)
    private String category;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "HIGHLIGHT_CONTENT", nullable = false, columnDefinition = "LONGTEXT")
    private String highlightContent;

    @Column(name = "DOC_ID", nullable = false)
    private String docId;

    @Column(name = "SCORE", nullable = false)
    private Double score;
    
    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "VIEW", nullable = false)
    private Long view;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDate createDt;

    @Column(name = "LAST_UPDATE_DT", nullable = false)
    private LocalDate lastUpdateDt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "lawEntity")
    private List<CommentEntity> commentList;



    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDate.now();
        this.view = 0L;
    }

}
