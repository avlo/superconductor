package com.prosilion.superconductor.repository.standard;

import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StandardTagEntityRepositoryRxR<T extends StandardTagEntityRxR> extends JpaRepository<T, Long> {
  String getCode();
//  List<T> findAllByEventId(Long eventId);
}