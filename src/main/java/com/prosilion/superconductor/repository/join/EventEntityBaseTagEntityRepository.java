package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntityBaseTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventEntityBaseTagEntityRepository extends JpaRepository<EventEntityBaseTagEntity, Long> {
//  List<Long> findAllById(Long id);
//  List<Long> findAllByEventId(Long eventId);
}