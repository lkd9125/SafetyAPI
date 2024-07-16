package com.safety.law.global.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "LOGGING")
@Data
public class LoggingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Integer idx;

    @Column(name = "TYPE", nullable = false, length = 20)
    private String type;

    @Column(name = "USERNAME", nullable = true, length = 500)
    private String username;

    @Column(name = "IP", nullable = false, length = 25)
    private String ip;

    @Column(name = "MESSAGE", nullable = true, length = 100)
    private String message;

    @Column(name = "CREDENTIALS", nullable = true, length = 500)
    private String credentials;

    @Column(name = "CREATE_DT", nullable = true)
    private LocalDateTime createDt = LocalDateTime.now();

}
