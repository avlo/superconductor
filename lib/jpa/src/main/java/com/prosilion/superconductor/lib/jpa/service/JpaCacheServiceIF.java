package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.jpa.dto.deletion.DeletionEventEntityJpaIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheServiceIF extends CacheServiceIF<Long, EventEntityIF> {
  Optional<EventEntityIF> getEventByUid(@NonNull Long id);
  List<DeletionEventEntityJpaIF> getAllDeletionEvents();
}
