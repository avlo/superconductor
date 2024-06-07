package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventEntityStandardTagEntityRepository<T extends EventEntityStandardTagEntity> extends JpaRepository<T, Long> {
  List<T> getAllByEventId(Long eventId);
  Character getCode();
}