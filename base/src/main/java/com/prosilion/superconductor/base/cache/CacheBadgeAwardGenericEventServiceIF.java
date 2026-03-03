package com.prosilion.superconductor.base.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends CacheTagMappedEventServiceIF<T>, EventMaterializer<BaseEvent> {
  String INVALID_REMOTE_URL = "EventTag [%s] is missing remote url";

  @Override
  T materialize(@NonNull EventIF eventIF);

  Optional<S> getEventTagEvent(@NonNull String eventId, @NonNull String url) throws JsonProcessingException;

  default Optional<S> getEventTagEvent(@NonNull EventTag eventTag) throws JsonProcessingException {
    return getEventTagEvent(
        eventTag.getIdEvent(),
        Optional.ofNullable(
                eventTag.getRecommendedRelayUrl())
            .orElseThrow(() ->
                new NostrException(
                    String.format(
                        INVALID_REMOTE_URL, eventTag))));
  }
}
