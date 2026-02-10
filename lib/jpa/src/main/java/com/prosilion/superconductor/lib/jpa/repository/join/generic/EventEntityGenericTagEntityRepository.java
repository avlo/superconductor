package com.prosilion.superconductor.lib.jpa.repository.join.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import com.prosilion.superconductor.lib.jpa.entity.join.generic.EventEntityGenericTagJpaEntity;

@Repository
public interface EventEntityGenericTagEntityRepository extends JpaRepository<EventEntityGenericTagJpaEntity, Long> {
  List<EventEntityGenericTagJpaEntity> findByEventId(Long eventId);
  List<EventEntityGenericTagJpaEntity> findByEventIdIn(Collection<Long> eventIds);
}
