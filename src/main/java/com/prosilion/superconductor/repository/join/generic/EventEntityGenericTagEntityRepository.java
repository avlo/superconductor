package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityGenericTagEntityRepository<T extends GenericTagEntity> extends JpaRepository<EventEntityGenericTagEntity, Long> {
  String getKey();
}