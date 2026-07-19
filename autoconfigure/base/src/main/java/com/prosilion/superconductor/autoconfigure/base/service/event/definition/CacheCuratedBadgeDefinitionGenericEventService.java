package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.CuratedBadgeDefinitionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheCuratedBadgeDefinitionEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
// TODO: rxr common elements from CacheCuratedBadgeAwardEventService into baseClass
public class CacheCuratedBadgeDefinitionGenericEventService implements CacheCuratedBadgeDefinitionEventServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  public CacheCuratedBadgeDefinitionGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheBadgeDefinitionGenericEventService = cacheBadgeDefinitionGenericEventService;
  }

  @Override
  public Optional<CuratedBadgeDefinitionEvent> materialize(@NonNull EventIF incomingCurationSetsEvent) {
    return Optional.of(new CuratedBadgeDefinitionEvent(incomingCurationSetsEvent.asGenericEventRecord()));
  }

  @Override
  public Optional<CuratedBadgeDefinitionEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheServiceIF.getEventByEventId(eventId)
       .flatMap(this::materialize)
       .or(() -> cacheBadgeDefinitionGenericEventService.getEvent(eventId, relay)
          .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionEvent> getBy(@NonNull EventTag eventTag) {
    return materializeFirst(
       cacheServiceIF.getEventsByKindAndEventTag(getKind(), eventTag))
       .or(() ->
          cacheBadgeDefinitionGenericEventService.getEvent(eventTag.eventId(), eventTag.requireRelay())
             .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventByKindAndAuthorPublicKeyAndIdentifierTag(getKind(), publicKey, identifierTag)
       .flatMap(this::materialize)
       .or(() ->
          cacheBadgeDefinitionGenericEventService.getBy(
                new PubKeyTag(publicKey), identifierTag)
             .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionEvent> getBy(@NonNull AddressTag addressTag) {
    return materializeFirst(
       cacheServiceIF.getEventsByKindAndAddressTag(getKind(), addressTag))
       .or(() ->
          cacheBadgeDefinitionGenericEventService.getBy(addressTag)
             .flatMap(this::materialize));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS;
  }

  @Override
  public GenericEventRecord save(EventIF event) {
    return cacheServiceIF.save(event);
  }
}
