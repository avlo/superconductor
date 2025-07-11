package com.prosilion.superconductor.repository.join;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EventEntityAbstractTagEntityRepository<T extends EventEntityAbstractEntity> extends CrudRepository<T, Long> {
  List<T> findByEventId(Long eventId);
  String getCode();
}
