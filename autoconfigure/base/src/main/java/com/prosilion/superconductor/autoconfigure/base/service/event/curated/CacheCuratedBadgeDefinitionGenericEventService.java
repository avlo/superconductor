package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionGenericEventServiceDecorIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
// TODO: rxr common elements from CacheCuratedBadgeAwardGenericEventService into baseClass
public class CacheCuratedBadgeDefinitionGenericEventService extends CacheCuratedEventService<CuratedBadgeDefinitionGenericEvent> implements CacheCuratedBadgeDefinitionGenericEventServiceDecorIF {
  private final CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  public CacheCuratedBadgeDefinitionGenericEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService) {
    super(cacheServiceIF);
    this.cacheBadgeDefinitionGenericEventService = cacheBadgeDefinitionGenericEventService;
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> materialize(@NonNull EventIF incomingCurationSetsEvent) {
    return Optional.of(new CuratedBadgeDefinitionGenericEvent(incomingCurationSetsEvent.asGenericEventRecord()));
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super.getEvent(eventId, relay)
       .or(() -> cacheBadgeDefinitionGenericEventService.getEvent(eventId, relay)
          .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getByDirect(@NonNull EventTag eventTag) {
    return materializeFirst(
       cacheServiceIF.getEventsByKindAndEventTag(getKind(), eventTag))
       .or(() ->
          cacheBadgeDefinitionGenericEventService.getEvent(eventTag.eventId(), eventTag.requireRelay())
             .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventByKindAndAuthorPublicKeyAndIdentifierTag(getKind(), publicKey, identifierTag)
       .flatMap(this::materialize)
       .or(() ->
          cacheBadgeDefinitionGenericEventService.getBy(new PubKeyTag(publicKey), identifierTag)
             .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedBadgeDefinitionGenericEvent> getBy(@NonNull AddressTag addressTag) {
    return materializeFirst(
       cacheServiceIF.getEventsByKindAndAddressTag(getKind(), addressTag))
       .or(() ->
          cacheBadgeDefinitionGenericEventService.getByExpanded(addressTag)
             .flatMap(this::materialize));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS;
  }
}
