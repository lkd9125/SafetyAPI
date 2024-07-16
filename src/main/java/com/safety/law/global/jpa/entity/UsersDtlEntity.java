package com.safety.law.global.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "USERS_DTL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "usersEntity")
public class UsersDtlEntity {

    @Id
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "NICKNAME", nullable = true)
    private String nickname;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "HPNO", nullable = true)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERNAME",  updatable = false, insertable = false)
    private UsersEntity usersEntity;

}
