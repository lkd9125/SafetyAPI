package com.safety.law.global.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safety.law.global.jpa.entity.UserLawReadHistoryEntity;

@Repository
public interface UserLawReadHistoryRepository extends JpaRepository<UserLawReadHistoryEntity, Long>{

    public Optional<Long> countByUsername(String username);

    public void deleteByUsername(String username);
}
