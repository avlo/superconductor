package com.prosilion.superconductor.lib.jpa.repository.join;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;

@NoRepositoryBean
public interface EventEntityAbstractTagJpaEntityRepository<T extends EventEntityAbstractJpaEntity> extends JpaRepository<T, Long> {
  List<T> findByEventId(Long eventId);
  List<T> findByEventIdIn(Collection<Long> eventIds);
  String getCode();
}
