package com.prosilion.superconductor.base.cache.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.EventTag;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.awaitility.core.DurationFactory;
import org.springframework.lang.NonNull;

public interface CacheDereferenceEventTagServiceIF extends CacheDereferenceAbstractTagServiceIF<EventTag> {
  @Override
  default Optional<GenericEventRecord> getEvent(EventTag t) throws JsonProcessingException {
    return getEvent(t, DurationFactory.of(3, TimeUnit.SECONDS));
  }

  Optional<GenericEventRecord> getEvent(EventTag t, Duration timeout) throws JsonProcessingException;

  default Optional<GenericEventRecord> getEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException {
    return getEvent(eventId, url, DurationFactory.of(3, TimeUnit.SECONDS));
  }

  default Optional<GenericEventRecord> getEvent(@NonNull String eventId, @NonNull String url, Duration timeout) throws JsonProcessingException {
    return getEvent(new EventTag(eventId, url), timeout);
  }
}
