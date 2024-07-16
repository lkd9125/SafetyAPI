package com.safety.law.global.jpa.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "SCHEDULER_UPDATE_DATE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchedulerUpdateDateEntity {

    @Id
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "UPDATE_DT", nullable = false)
    private LocalDate updateDt;

    @Column(name = "STATUS", nullable = false)
    private String status;

}
