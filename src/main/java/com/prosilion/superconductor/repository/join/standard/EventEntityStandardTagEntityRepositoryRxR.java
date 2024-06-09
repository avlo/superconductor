package com.prosilion.superconductor.repository.join.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EventEntityStandardTagEntityRepositoryRxR<T extends EventEntityStandardTagEntityRxR> extends JpaRepository<T, Long> {
  List<T> getAllByEventId(Long eventId);
  String getCode();
}