package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.GenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenericTagEntityRepository<T extends GenericTagEntity> extends JpaRepository<T, Long> {
  String getCode();
}