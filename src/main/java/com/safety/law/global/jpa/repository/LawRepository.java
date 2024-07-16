package com.safety.law.global.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safety.law.global.jpa.entity.LawEntity;

@Repository
public interface LawRepository extends JpaRepository<LawEntity, Long>{

    public List<LawEntity> findByDocIdIn(List<String> docIds);
    
}
