package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface JpaCacheServiceIF extends CacheServiceIF {
  Optional<? extends BaseEvent> getEventByUid(Long id);
  Optional<? extends BaseEvent> getEvent(@NonNull EventIF eventIF);
  List<Long> getAllDeletionEventIds();
}
