package com.safety.law.global.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.safety.law.global.jpa.entity.SchedulerUpdateDateEntity;

@Repository
public interface SchedulerUpdateDateRepository extends JpaRepository<SchedulerUpdateDateEntity, String>{

}
