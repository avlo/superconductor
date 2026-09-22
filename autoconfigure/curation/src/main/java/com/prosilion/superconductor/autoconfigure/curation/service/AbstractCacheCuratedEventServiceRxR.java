package com.prosilion.superconductor.autoconfigure.curation.service;

import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheAddressableEventServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCacheCuratedEventServiceRxR<
   T extends AbstractSetsEvent,
   U extends BaseEvent,
   V extends ReferencedAbstractEventTag> implements
   CacheTagMappedEventServiceIF<T, V>,
   CacheCuratedEventServiceIF<T>,
   CacheAddressableEventServiceIF<T, V> {
  private final CacheServiceIF cacheServiceIF;

  @Getter
  private final Relay relay;
  @Getter
  private final Identity instanceIdentity;

  public AbstractCacheCuratedEventServiceRxR(
     @NonNull Identity instanceIdentity,
     @NonNull String relay,
     @NonNull CacheServiceIF cacheServiceIF) {
    this.instanceIdentity = instanceIdentity;
    this.relay = new Relay(relay);
    this.cacheServiceIF = cacheServiceIF;
  }

  protected abstract T createFrom(@NonNull GenericEventRecord eventRecord);
  protected abstract T createFromFetched(@NonNull U baseEvent, @NonNull Relay relay);

  @Override
  public final Optional<T> materialize(@NonNull EventIF event) {
    return Optional.of(createFrom(event.asGenericEventRecord()));
  }

  @Override
  public final Optional<T> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return cacheServiceIF.getEventByEventId(eventId).flatMap(this::materialize);
  }

  protected final Optional<T> findFirstByEventTag(@NonNull EventTag eventTag) {
    return cacheServiceIF.getFirstEventByKindAndEventTag(getKind(), eventTag).flatMap(this::materialize);
  }

  protected final Optional<T> findFirstByAddressTag(@NonNull AddressTag addressTag) {
    return cacheServiceIF.getFirstEventByKindAndAddressTag(getKind(), addressTag).flatMap(this::materialize);
  }

  protected final Optional<T> findFirstByPubKeyTagAndIdentifierTag(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(getKind(), pubKeyTag, identifierTag)
       .stream().findFirst().flatMap(this::materialize);
  }

  protected final Optional<T> findByAuthorAndIdentifierTag(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag) {
    return cacheServiceIF.getEventByKindAndAuthorPublicKeyAndIdentifierTag(getKind(), publicKey, identifierTag).flatMap(this::materialize);
  }

  protected final List<T> findByPubKeyTag(@NonNull PubKeyTag pubKeyTag) {
    return materializeList(
       cacheServiceIF.getEventsByKindAndPubKeyTag(getKind(), pubKeyTag)).toList();
  }

  protected final Optional<T> findFirstByPubKeyTagAndEventTag(@NonNull PubKeyTag pubKeyTag, @NonNull EventTag eventTag) {
    return materializeFirst(cacheServiceIF.getEventsByKindAndPubKeyTagAndEventTag(getKind(), pubKeyTag, eventTag));
  }

  protected final List<T> findByPubKeyTagAndIdentifierTag(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag) {
    return
       materializeList(
          cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(
             getKind(), pubKeyTag, identifierTag)).toList();
  }

  protected final Optional<T> findFirstByPubKeyTagAndAddressTag(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return
       materializeFirst(
          cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(
             getKind(), pubKeyTag, addressTag));
  }

  protected final Optional<T> findOrCurate(
     @NonNull Supplier<Optional<T>> localLookup,
     @NonNull Supplier<Optional<U>> sourceLookup,
     @NonNull Function<U, Relay> referenceRelayFxn) {
    log.debug("inside findOrCurate(localLookup, sourceLookup, referenceRelayFxn) ...");
    Optional<T> localEvent = localLookup.get();
    localEvent.ifPresentOrElse(t -> log.debug("found localEvent:\n{}", t.createPrettyPrintJson()),
       () -> log.debug("local event not found, trying sourceLookup ..."));
    return localEvent
       .or(() -> curate(sourceLookup.get(), referenceRelayFxn));
  }

  private Optional<T> curate(Optional<U> sourceEvent, Function<U, Relay> referenceRelayFxn) {
    return sourceEvent
       .map(event -> {
         T fromFetched = createFromFetched(event, referenceRelayFxn.apply(event));
         log.debug("successfully fetched event:\n{}", fromFetched.createPrettyPrintJson());
         return fromFetched;
       })
       .map(event -> {
         log.debug("saving fetched event:\n{}", event.createPrettyPrintJson());
         cacheServiceIF.save(event);
         return event;
       });
  }
}
