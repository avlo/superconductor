package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.AbstractTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractTagEntityRepository<T extends AbstractTagEntity> extends JpaRepository<T, Long> {
  String getCode();
//  List<T> findAllByEventId(Long eventId);
}