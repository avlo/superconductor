package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventEntityGenericTagEntityRepository extends CrudRepository<EventEntityGenericTagEntity, Long> {
  List<EventEntityGenericTagEntity> findByEventId(Long eventId);
}