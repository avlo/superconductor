package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.service.event.CacheBadgeDefinitionAbstractEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.NonNull;

public abstract class CacheBadgeDefinitionAbstractEventService<T extends AddressableEvent> implements CacheBadgeDefinitionAbstractEventServiceIF<T> {
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

  @Override
  public final Optional<T> materialize(@NonNull EventIF eventIF) {
    return Optional.of(createBadgeDefinitionEvent(eventIF.asGenericEventRecord()));
  }

  protected abstract T createBadgeDefinitionEvent(@NonNull GenericEventRecord eventRecord);

  public Optional<T> getEvent(@NonNull String eventId, @NonNull Relay backupRelay) {
    return cacheServiceIF.getEventByEventId(eventId).flatMap(this::materialize)
       .or(() ->
          cacheServiceIF.getEventsByKindAndEventTag(
                getKind(),
                new EventTag(eventId)).stream().findFirst()
             .flatMap(this::materialize))
       .or(() -> cacheReferenceEventTagServiceIF.getEvent(eventId, backupRelay)
          .flatMap(this::materialize));
  }

  @Override
  public Optional<T> getByExpanded(@NonNull AddressTag addressTag) {
    if (!addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
      throw new NostrException(
         String.format("invalid addressTag.getKind(): [%s] for DefinitionAbstractEvent.  must be kind type [%s]", addressTag.getKind(), Kind.BADGE_DEFINITION_EVENT));

    Optional<GenericEventRecord> by = cacheReferenceAddressTagServiceIF.getByExpanded(addressTag);
    return by.flatMap(this::materialize);
  }

  protected final Optional<GenericEventRecord> findFirstByPubKeyAndIdentifier(
     @NonNull PubKeyTag pubKeyTag,
     @NonNull IdentifierTag identifierTag,
     @NonNull Predicate<GenericEventRecord> predicate) {
    return cacheServiceIF
       .getEventsByKindAndPubKeyTagAndIdentifierTag(
          getKind(), pubKeyTag, identifierTag)
       .stream()
       .filter(predicate)
       .findFirst();
  }

  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
