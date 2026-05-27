package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import static com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService.NON_EXISTENT_ADDRESS_TAG;

@Slf4j
public class CacheBadgeDefinitionReputationEventService extends CacheBadgeDefinitionAbstractEventService<BadgeDefinitionReputationEvent> implements CacheBadgeDefinitionReputationEventServiceIF {
  private final CacheFormulaEventService cacheFormulaEventService;

  public CacheBadgeDefinitionReputationEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF,
      @NonNull CacheFormulaEventService cacheFormulaEventService) {
    super(cacheDereferenceEventTagServiceIF, cacheDereferenceAddressTagServiceIF);
    this.cacheFormulaEventService = cacheFormulaEventService;
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    log.debug("... materialize(incomingBadgeDefinitionReputationEvent)...\n{}", incomingBadgeDefinitionReputationEvent.createPrettyPrintJson());

    List<FormulaEvent> formulaEvents = getFormulaEvents(incomingBadgeDefinitionReputationEvent.asGenericEventRecord());
    log.debug("... formulaEvent count: [{}]", formulaEvents.size());
    log.debug("... materialized formulaEvents ...\n  {}", formulaEvents.stream().map(EventIF::asGenericEventRecord).map(GenericEventRecord::createPrettyPrintJson).toList());

    log.debug("formulaEvents as addressTags:");
    formulaEvents.stream().map(AddressableEvent::asAddressTag).map(Util::prettyPrintAddressTags).toList().forEach(log::debug);

    return new BadgeDefinitionReputationEvent(
        incomingBadgeDefinitionReputationEvent.asGenericEventRecord(), addressTag ->
        formulaEvents.stream().filter(formulaEvent ->
            formulaEvent.asAddressTag().equals(addressTag)).findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, incomingBadgeDefinitionReputationEvent))));
  }

//  TODO: refactor back in after debug
  private List<FormulaEvent> getFormulaEvents(@NonNull GenericEventRecord badgeDefinitionReputationEventGER) {
    log.debug("getFormulaEvents(@NonNull GenericEventRecord badgeDefinitionReputationEventGER):\n{}", badgeDefinitionReputationEventGER.createPrettyPrintJson());

    List<AddressTag> addressTagsOfFormulaEvents = badgeDefinitionReputationEventGER.getTypeSpecificTagStream(AddressTag.class).toList();
    log.debug("addressTagsOfFormulaEventsOriginalList.toList() size:  [{}]", addressTagsOfFormulaEvents.size());
    log.debug("addressTagsOfFormulaEventsOriginalList.toList():{}", Util.prettyPrintAddressTags(addressTagsOfFormulaEvents));

    if (addressTagsOfFormulaEvents.isEmpty()) {
      log.debug("addressTagsOfFormulaEvents was Empty. throwing exception");
      throw new NostrException(
          String.format(NON_EXISTENT_ADDRESS_TAG, badgeDefinitionReputationEventGER));
    }

    List<FormulaEvent> formulaEvents = addressTagsOfFormulaEvents.stream()
        .map(cacheFormulaEventService::getAddressTagAsFormulaEvent)
        .flatMap(Optional::stream).toList();
    log.debug("cacheFormulaEventService::getAddressTagAsFormulaEvent returned:\n  {}",
        formulaEvents.stream().map(EventIF::asGenericEventRecord).map(GenericEventRecord::createPrettyPrintJson).toList());

    log.debug("addressTagsOfFormulaEvents.size(): [{}], formulaEvents.size(): [{}]",
        addressTagsOfFormulaEvents.size(), formulaEvents.size());

    if (!Objects.equals(addressTagsOfFormulaEvents.size(), formulaEvents.size()))
      throw new NostrException(
          String.format("Unequal count AddressTags vs FormulaEvents:%s\nFormulaEvent:\n%s",
              Util.prettyPrintAddressTags(addressTagsOfFormulaEvents),
              Util.prettyPrintGenericEventRecords(formulaEvents.stream().map(FormulaEvent::getGenericEventRecord).toList())));

    log.debug("formulaEvents size matches addressTag size, return formulaEvents");
    return formulaEvents;
  }
}
