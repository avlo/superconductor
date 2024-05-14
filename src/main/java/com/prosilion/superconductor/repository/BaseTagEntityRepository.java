package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.BaseTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseTagEntityRepository extends JpaRepository<BaseTagEntity, Long> {
}