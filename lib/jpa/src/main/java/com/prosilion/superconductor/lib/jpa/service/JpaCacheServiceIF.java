package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheServiceIF extends CacheServiceIF<Long, EventJpaEntityIF> {
  Optional<EventJpaEntityIF> getEventByUid(@NonNull Long id);
  List<DeletionEventJpaEntityIF> getAllDeletionEvents();
}
