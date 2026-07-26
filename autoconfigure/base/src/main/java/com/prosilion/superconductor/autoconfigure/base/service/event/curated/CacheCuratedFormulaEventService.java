package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
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
  private final CacheFormulaEventServiceIF cacheFormulaEventServiceIF;

  public CacheCuratedFormulaEventService(
     @NonNull Identity instanceIdentity,
     @NonNull String relayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheFormulaEventServiceIF cacheFormulaEventServiceIF) {
    super(instanceIdentity, relayUrl, cacheServiceIF);
    this.cacheFormulaEventServiceIF = cacheFormulaEventServiceIF;
  }

  @Override
  protected CuratedFormulaEvent createFrom(@NonNull GenericEventRecord eventRecord) {
    return new CuratedFormulaEvent(eventRecord);
  }

  @Override
  public Optional<CuratedFormulaEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    return findOrCurate(
       () -> findFirstByPubKeyAndIdentifier(pubKeyTag, identifierTag),
       () -> cacheFormulaEventServiceIF.getBy(
          pubKeyTag.getPublicKey(), identifierTag, relay),
       ignored -> relay);
  }

  @Override
  public Optional<CuratedFormulaEvent> getByDirect(@NonNull EventTag eventTag) {
    return findOrCurate(
       () -> findFirstByEventTag(eventTag),
       () -> cacheFormulaEventServiceIF.getEvent(
          eventTag.getEventId(), eventTag.requireRelay()),
       formulaEvent -> formulaEvent.getRelay().orElseThrow());
  }

  @Override
  public Optional<CuratedFormulaEvent> getByDirect(@NonNull AddressTag addressTag) {
    return findOrCurate(
       () -> findFirstByAddressTag(addressTag),
       () -> cacheFormulaEventServiceIF.getByDirect(addressTag),
       formulaEvent -> formulaEvent.getRelay().orElseThrow());
  }

  @Override
  protected CuratedFormulaEvent createFromFetched(
     @NonNull FormulaEvent formulaEvent,
     @NonNull Relay relay) {
    return new CuratedFormulaEvent(
       instanceIdentity,
       formulaEvent,
       new ReferenceTag(relay.getUrl()),
       relay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_FORMULA_EVENT;
  }
}
