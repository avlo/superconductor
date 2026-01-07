package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.service.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.NotifierService;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardReputationEventKindTypeRedisPlugin extends PublishingEventKindTypePlugin {
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;
  private final CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventService;

  public BadgeAwardReputationEventKindTypeRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindTypePluginIF eventKindTypePlugin,
      @NonNull CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventService,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    super(notifierService, eventKindTypePlugin);
    this.cacheBadgeAwardReputationEventService = cacheBadgeAwardReputationEventService;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    AddressTag addressTagsOfBadgeAwardReputationEvent = Filterable.getTypeSpecificTags(AddressTag.class, incomingBadgeAwardReputationEvent).stream().findFirst().orElseThrow();

    GenericEventRecord badgeDefinitionReputationEventGenericEventRecord = cacheDereferenceAddressTagServiceIF.getEvent(addressTagsOfBadgeAwardReputationEvent).orElseThrow();
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = cacheBadgeAwardReputationEventService.getBadgeDefinitionReputationEvent(badgeDefinitionReputationEventGenericEventRecord);

    Function<AddressTag, BadgeDefinitionReputationEvent> fxn = addressTag ->
        badgeDefinitionReputationEvent;

    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent((GenericEventRecord) incomingBadgeAwardReputationEvent, fxn);
    BadgeAwardReputationEvent reconstructedBadgeAwardReputationEvent = cacheBadgeAwardReputationEventService.reconstruct(badgeAwardReputationEvent);
    super.processIncomingEvent(reconstructedBadgeAwardReputationEvent);
  }
}
