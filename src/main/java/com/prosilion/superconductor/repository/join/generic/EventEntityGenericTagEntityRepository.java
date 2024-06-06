package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EventEntityGenericTagEntityRepository<T extends EventEntityGenericTagEntity> extends JpaRepository<T, Long> {
  List<T> getAllByEventId(Long eventId);
  Character getCode();
}