package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StandardTagEntityRepository<T extends StandardTagEntity> extends JpaRepository<T, Long> {
  String getCode();
//  List<T> findAllByEventId(Long eventId);
}