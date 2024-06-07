package com.prosilion.superconductor.repository;

import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StandardTagEntityRepository<T extends StandardTagEntity> extends JpaRepository<T, Long> {
  Character getCode();
}