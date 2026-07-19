package com.prosilion.superconductor.autoconfigure.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.cache.CacheFormulaEventServiceIF;
import java.util.Optional;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;

public class CacheCuratedFormulaEventService implements CacheFormulaEventServiceIF {
  private final CacheFormulaEventService cacheFormulaEventService;
  private final CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService;

  public CacheCuratedFormulaEventService(
     @NonNull CacheFormulaEventService cacheFormulaEventService,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService) {
    this.cacheFormulaEventService = cacheFormulaEventService;
    this.cacheCuratedBadgeDefinitionGenericEventService = cacheCuratedBadgeDefinitionGenericEventService;
  }

  private final Function<CuratedBadgeDefinitionGenericEvent, FormulaEvent> curatedBadgeDefnToFormulaFxn =
     curatedBadgeDefinitionEvent -> new FormulaEvent(
        curatedBadgeDefinitionEvent.asGenericEventRecord(),
        addressTag -> new BadgeDefinitionGenericEvent(curatedBadgeDefinitionEvent.asGenericEventRecord()));

  @Override
  public Optional<FormulaEvent> materialize(@NonNull EventIF incomingFormulaEvent) {
    return cacheCuratedBadgeDefinitionGenericEventService
       .getBy(incomingFormulaEvent.requireFirstTag(AddressTag.class))
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.materialize(incomingFormulaEvent));
  }

  @Override
  public Optional<FormulaEvent> getBy(@NonNull PublicKey publicKey, @NonNull IdentifierTag identifierTag, @NonNull Relay relay) {
    return cacheCuratedBadgeDefinitionGenericEventService
       .getBy(publicKey, identifierTag)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getBy(publicKey, identifierTag, relay));
  }

  @Override
  public Optional<FormulaEvent> getByDirect(@NonNull AddressTag addressTag) {
    return cacheCuratedBadgeDefinitionGenericEventService
       .getBy(addressTag)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getByDirect(addressTag));
  }

  @Override
  public Kind getKind() {
    return cacheFormulaEventService.getKind();
  }

  @Override
  public Optional<FormulaEvent> getEvent(@NonNull String s, @NonNull Relay relay) {
    return cacheCuratedBadgeDefinitionGenericEventService
       .getEvent(s, relay)
       .map(curatedBadgeDefnToFormulaFxn)
       .or(() -> cacheFormulaEventService.getEvent(s, relay));
  }
}
