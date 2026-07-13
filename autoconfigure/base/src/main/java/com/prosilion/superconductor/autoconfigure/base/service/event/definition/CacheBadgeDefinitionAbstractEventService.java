package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionAbstractEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CacheBadgeDefinitionAbstractEventService<T extends BadgeDefinitionGenericEvent> implements CacheBadgeDefinitionAbstractEventServiceIF<T> {
  private final CacheServiceIF cacheServiceIF;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  private final CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;

  public CacheBadgeDefinitionAbstractEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheReferenceAddressTagServiceIF = cacheReferenceAddressTagServiceIF;
  }

  public abstract Optional<T> materialize(@NonNull EventIF eventIF);

  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheReferenceEventTagServiceIF.getEvent(eventId, relay).flatMap(this::materialize)
       .or(() ->
          cacheServiceIF.getEventsByKindAndEventTag(
                getKind(),
                new EventTag(eventId)).stream().findFirst()
             .flatMap(this::materialize));
  }

  public Optional<T> getBy(@NonNull AddressTag addressTag) {
    if (!addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
      throw new NostrException(
         String.format("invalid addressTag.getKind(): [%s] for DefinitionAbstractEvent.  must be kind type [%s]", addressTag.getKind(), Kind.BADGE_DEFINITION_EVENT));

    return cacheReferenceAddressTagServiceIF.getBy(addressTag)
       .flatMap(genericEventRecord ->
          getEvent(
             genericEventRecord.getId(),
             genericEventRecord.requireFirstTag(RelayTag.class).getRelay()));
  }

  public Optional<T> getBy(@NonNull AddressTag addressTag, @NonNull PubKeyTag pubKeyTag) {
    return getBy(addressTag).filter(event -> event.requireFirstTag(PubKeyTag.class).equals(pubKeyTag));
  }

  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
