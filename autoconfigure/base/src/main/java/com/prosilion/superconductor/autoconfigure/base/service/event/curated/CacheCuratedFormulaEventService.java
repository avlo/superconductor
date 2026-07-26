package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceIF;
import java.util.Optional;
import org.jspecify.annotations.NonNull;

public class CacheCuratedFormulaEventService extends AbstractCacheCuratedEventService<CuratedFormulaEvent, FormulaEvent> implements CacheCuratedFormulaEventServiceIF {
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;

  public CacheCuratedFormulaEventService(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF) {
    super(cacheServiceIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
  }

  @Override
  public Optional<CuratedFormulaEvent> materialize(@NonNull EventIF incomingCuratedFormulaEvent) {
    return Optional.of(new CuratedFormulaEvent(incomingCuratedFormulaEvent.asGenericEventRecord()));
  }

  @Override
  public Optional<CuratedFormulaEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super.getEvent(eventId, relay)
       .or(() ->
          cacheFormulaEventServiceIF.getEvent(eventId, relay)
             .map(formulaEvent -> createFromFetched(
                formulaEvent, relay))
             .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedFormulaEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(
          getKind(),
          pubKeyTag,
          identifierTag)
       .stream().findFirst()
       .flatMap(this::materialize)
       .or(() -> cacheFormulaEventServiceIF.getBy(pubKeyTag.getPublicKey(), identifierTag, relay)
          .map(formulaEvent -> createFromFetched(
             formulaEvent, relay))
          .flatMap(this::materialize));
  }

  @Override
  public Optional<CuratedFormulaEvent> getByDirect(@NonNull EventTag eventTag) {
    return cacheServiceIF.getEventsByKindAndEventTag(getKind(), eventTag)
       .stream().findFirst().flatMap(this::materialize)
       .or(() -> {
         Optional<FormulaEvent> formulaEvent1 = cacheFormulaEventServiceIF.getEvent(eventTag.getEventId(), eventTag.requireRelay());
         return formulaEvent1
            .map(formulaEvent -> createFromFetched(
               formulaEvent, formulaEvent.getRelay().orElseThrow()))
            .flatMap(this::materialize);
       });
  }

  @Override
  public Optional<CuratedFormulaEvent> getByDirect(@NonNull AddressTag addressTag) {
    return cacheServiceIF.getEventsByKindAndAddressTag(getKind(), addressTag)
       .stream().findFirst().flatMap(this::materialize)
       .or(() -> cacheFormulaEventServiceIF.getByDirect(addressTag)
          .map(formulaEvent -> createFromFetched(
             formulaEvent, formulaEvent.getRelay().orElseThrow()))
          .flatMap(this::materialize));
  }

  @Override
  public CuratedFormulaEvent createFromFetched(
     @NonNull FormulaEvent formulaEvent,
     @NonNull Relay relay) {
    return new CuratedFormulaEvent(
       superconductorInstanceIdentity,
       formulaEvent,
       new ReferenceTag(relay.getUrl()),
       new Relay(superconductorRelayUrl));
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_FORMULA_EVENT;
  }
}
