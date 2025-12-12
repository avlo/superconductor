package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.TagMappedEventIF;
import java.util.Optional;

public interface CacheTagMappedEventServiceIF<T extends TagMappedEventIF> {
  void save(T event);
  Optional<T> getEvent(String eventId);
  Kind getKind();
}
