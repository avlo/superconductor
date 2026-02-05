package com.prosilion.superconductor.base.cache;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CacheBadgeAwardGenericEventServiceIF
    <S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends CacheTagMappedEventServiceIF<T> {
  String INVALID_REMOTE_URL = "EventTag [%s] is missing remote url";

  @Override
  T materialize(@NonNull EventIF eventIF) throws NostrException;

  Optional<S> getEventTagEvent(@NonNull String eventId, @NonNull String url);

  default Optional<S> getEventTagEvent(@NonNull EventTag eventTag) {
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
