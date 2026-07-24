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
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceIF;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

public class CacheCuratedFormulaEventService extends CacheCuratedEventService<FormulaEvent> implements CacheCuratedFormulaEventServiceIF {
  private final CacheFormulaEventService cacheFormulaEventService;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;

  public CacheCuratedFormulaEventService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheFormulaEventService cacheFormulaEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF) {
    super(cacheServiceIF);
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
  }

  private final Function<CuratedBadgeDefinitionGenericEvent, FormulaEvent> curatedBadgeDefnToFormulaFxn =
     curatedBadgeDefinitionEvent -> new FormulaEvent(
        curatedBadgeDefinitionEvent.asGenericEventRecord(),
        addressTag -> new BadgeDefinitionGenericEvent(curatedBadgeDefinitionEvent.asGenericEventRecord()));

  @Override
  public Optional<FormulaEvent> materialize(@NonNull EventIF incomingFormulaEvent) {
    return cacheCuratedBadgeDefinitionGenericEventServiceIF
       .getBy(incomingFormulaEvent.requireFirstTag(AddressTag.class))
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.materialize(incomingFormulaEvent));
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String eventId, @NonNull Relay relay) {
    return super.getEvent(eventId, relay)
       .or(() ->
          cacheFormulaEventService.getEvent(eventId, relay));
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    return cacheCuratedBadgeDefinitionGenericEventServiceIF
       .getBy(publicKey, identifierTag)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getBy(publicKey, identifierTag, relay));
  }

  @Override
  public Optional<FormulaEvent> getByDirect(@NonNull AddressTag addressTag) {
    return cacheCuratedBadgeDefinitionGenericEventServiceIF
       .getBy(addressTag)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getByDirect(addressTag));
  }

  @Override
  public Kind getKind() {
    return cacheFormulaEventService.getKind();
  }
}
