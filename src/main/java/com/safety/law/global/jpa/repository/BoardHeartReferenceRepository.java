package com.safety.law.global.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safety.law.global.jpa.entity.BoardHeartReferenceEntity;

@Repository
public interface BoardHeartReferenceRepository extends JpaRepository<BoardHeartReferenceEntity, Long>{

}
