package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EventEntityGenericTagEntityRepository<T extends EventEntityGenericTagEntity> extends JpaRepository<T, Long> {
  Character getCode();
}