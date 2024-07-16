package com.safety.law.global.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safety.law.global.jpa.entity.UsersDtlEntity;

@Repository
public interface UsersDtlRepository extends JpaRepository<UsersDtlEntity, String>{

    public Long countByNickname(String nickname);

}
