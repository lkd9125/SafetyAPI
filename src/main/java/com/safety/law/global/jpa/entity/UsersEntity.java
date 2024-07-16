package com.safety.law.global.jpa.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
public class UsersEntity implements UserDetails{

    @Id
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = true)
    private String email;

    @Column(name = "REFRESH_TOKEN", nullable = true)
    private String refreshToken;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled;

    @Column(name = "ACCOUNT_NON_LOCK", nullable = false)
    private boolean accountNonLock;

    @Column(name = "ACCOUNT_NON_EXPRIED", nullable = false)
    private boolean accountNonExpired;

    @Column(name = "PASS_FAIL_COUNT", nullable = false)
    private Integer passFailCount;

    @Column(name = "CREATE_DT", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "UPDATE_DT", nullable = false)
    private LocalDateTime updateDt;

    @Column(name = "DELETE_DT", nullable = true)
    private LocalDateTime deleteDt;

    @Column(name = "FCM_DEVICE_TOKEN", nullable = true)
    private String fcmDeviceToken;

    @Column(name = "PROFILE_IMG_URL", nullable = true)
    private String profileImgUrl;


    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    private Set<AuthoritiesEntity> authorities;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    private UsersDtlEntity usersDtl;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "usersEntity")
    private List<BoardHeartReferenceEntity> boardHeartReferenceEntities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @PrePersist
    protected void onCreate() {
        this.createDt = LocalDateTime.now();
        this.updateDt = LocalDateTime.now();
    }

    public void setAuthorities(AuthoritiesEntity authoritiesEntity) {

        if(authorities == null){
            this.authorities = new HashSet<>();
        }

        this.authorities.add(authoritiesEntity);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
