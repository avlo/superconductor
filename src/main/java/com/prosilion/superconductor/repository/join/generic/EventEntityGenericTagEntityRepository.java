package com.prosilion.superconductor.repository.join.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventEntityGenericTagEntityRepository extends JpaRepository<EventEntityGenericTagEntity, Long> {
  List<EventEntityGenericTagEntity> findByEventId(Long eventId);
}