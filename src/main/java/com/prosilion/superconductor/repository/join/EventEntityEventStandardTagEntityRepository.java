package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntityEventStandardTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventEntityEventStandardTagEntityRepository extends JpaRepository<EventEntityEventStandardTagEntity, Long> {
//  List<Long> findAllById(Long id);
//  List<Long> findAllByEventId(Long eventId);
}