package com.safety.law.global.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safety.law.global.jpa.entity.TokenBlackEntity;

@Repository
public interface TokenBlackRepository extends JpaRepository<TokenBlackEntity, Long>{

    public Long countByUsernameAndRefreshToken(String username, String refreshToken);

}
