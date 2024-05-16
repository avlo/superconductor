package com.prosilion.superconductor.repository.generic;

import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericTagEntityRepository<T extends GenericTagEntity> extends JpaRepository<T, Long> {
  default String getCode() {
    return "X";
  }
}