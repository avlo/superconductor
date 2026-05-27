package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class CacheBadgeAwardAbstractEventService<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> {
  private static final String MISSING_EVENT_TAG = "BadgeDefinitionAbstractEvent with eventId [%s] and url [%s] not found";
  private static final String MISSING_RELAY_TAG = "GenericEventRecord:\n  %s\ndoes not contain a RelayTag";
  private static final String MISSING_RELAY_TAG_URL = "RelayTag: [%s] missing required url";
  private static final String EMPTY_OPTIONAL = "[EMPTY OPTIONAL]";
  protected final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;

  public CacheBadgeAwardAbstractEventService(CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
  }

  public Optional<T> getEvent(@NonNull String eventId, @NonNull String url) {
    log.debug("... calling cacheDereferenceEventTagServiceIF.getEvent(eventId, url): [{}], [{}]", eventId, url);

    Optional<GenericEventRecord> unpopulatedEvent = cacheDereferenceEventTagServiceIF.getEvent(eventId, url);
    log.debug("returned pre-materialized optGER:\n{}",
        unpopulatedEvent.map(GenericEventRecord::createPrettyPrintJson).orElse(EMPTY_OPTIONAL));

    log.debug("... calling unpopulatedEvent.map(this::materialize ...");
    Optional<T> t = unpopulatedEvent.map(this::materialize);
    log.debug("... returned materialized event type [{}]:\n{}",
        t.map(T::getClass).map(Class::getSimpleName),
        t.map(EventIF::createPrettyPrintJson).orElse(EMPTY_OPTIONAL));

    return t;
  }

  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }

  protected abstract T materialize(@NonNull EventIF eventIF);

  protected abstract Optional<S> getBadgeDefinitionEvent(@NonNull GenericEventRecord genericEventRecord);

  public static String getRelayTagUrl(@NonNull GenericEventRecord genericEventRecord) {
    return genericEventRecord.getTypeSpecificTagStream(RelayTag.class)
        .findFirst()
        .map(relayTag ->
            Optional.of(relayTag).orElseThrow(() ->
                new NostrException(String.format(
                    MISSING_RELAY_TAG, genericEventRecord.createPrettyPrintJson()))))
        .map(RelayTag::getRelay)
        .map(Relay::getUrl).orElseThrow(() ->
            new NostrException(String.format(
                MISSING_RELAY_TAG_URL, genericEventRecord.createPrettyPrintJson())));
  }
}
