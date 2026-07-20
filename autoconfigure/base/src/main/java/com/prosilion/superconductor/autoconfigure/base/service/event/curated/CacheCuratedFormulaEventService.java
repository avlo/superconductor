package com.prosilion.superconductor.autoconfigure.base.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionGenericEventServiceDecorIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceDecorIF;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

public class CacheCuratedFormulaEventService extends CacheCuratedEventService<FormulaEvent> implements CacheCuratedFormulaEventServiceDecorIF {
  private final CacheFormulaEventService cacheFormulaEventService;
  private final CacheCuratedBadgeDefinitionGenericEventServiceDecorIF cacheCuratedBadgeDefinitionGenericEventServiceDecorIF;

  public CacheCuratedFormulaEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheFormulaEventService cacheFormulaEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceDecorIF cacheCuratedBadgeDefinitionGenericEventServiceDecorIF) {
    super(cacheServiceIF);
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.cacheCuratedBadgeDefinitionGenericEventServiceDecorIF = cacheCuratedBadgeDefinitionGenericEventServiceDecorIF;
  }

  private final Function<CuratedBadgeDefinitionGenericEvent, FormulaEvent> curatedBadgeDefnToFormulaFxn =
     curatedBadgeDefinitionEvent -> new FormulaEvent(
        curatedBadgeDefinitionEvent.asGenericEventRecord(),
        addressTag -> new BadgeDefinitionGenericEvent(curatedBadgeDefinitionEvent.asGenericEventRecord()));

  @Override
  public Optional<FormulaEvent> materialize(@NonNull EventIF incomingFormulaEvent) {
    return cacheCuratedBadgeDefinitionGenericEventServiceDecorIF
       .getBy(incomingFormulaEvent.requireFirstTag(AddressTag.class))
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.materialize(incomingFormulaEvent));
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super.getEvent(eventId, relay)
       .or(() -> cacheCuratedBadgeDefinitionGenericEventServiceDecorIF.getEvent(eventId, relay)
          .map(curatedBadgeDefnToFormulaFxn))
       .or(() ->
          cacheFormulaEventService.getEvent(eventId, relay));
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    return cacheCuratedBadgeDefinitionGenericEventServiceDecorIF
       .getBy(publicKey, identifierTag)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getBy(publicKey, identifierTag, relay));
  }

  @Override
  public Optional<FormulaEvent> getByDirect(@NonNull AddressTag addressTag) {
    return cacheCuratedBadgeDefinitionGenericEventServiceDecorIF
       .getBy(addressTag)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getByDirect(addressTag));
  }

  @Override
  public Kind getKind() {
    return cacheFormulaEventService.getKind();
  }
}
