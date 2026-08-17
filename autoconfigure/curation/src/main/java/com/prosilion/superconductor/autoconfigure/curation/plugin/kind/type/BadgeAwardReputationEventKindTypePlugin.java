package com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type;

import com.prosilion.nostr.event.DeletionEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.UniqueAddressTagEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.ReputationCalculationServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.PublishingEventKindTypePlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardReputationEventKindTypePlugin extends PublishingEventKindTypePlugin {
  private final String afterimageRelayUrl;
  private final Identity aImgIdentity;
  private final CacheServiceIF cacheServiceIF;
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final ReputationCalculationServiceIF reputationCalculationServiceIF;

  public BadgeAwardReputationEventKindTypePlugin(
     @NonNull String afterimageRelayUrl,
     @NonNull Identity aImgIdentity,
     @NonNull NotifierService notifierService,
     @NonNull EventKindTypePluginIF eventKindTypePlugin,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull ReputationCalculationServiceIF reputationCalculationServiceIF,
     @NonNull CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF) {
    super(notifierService, eventKindTypePlugin);
    this.afterimageRelayUrl = afterimageRelayUrl;
    this.aImgIdentity = aImgIdentity;
    this.cacheServiceIF = cacheServiceIF;
    this.reputationCalculationServiceIF = reputationCalculationServiceIF;
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
    log.debug("using afterimageRelayUrl: [{}]", afterimageRelayUrl);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingFollowSetsEventAsReputationEvent, @NonNull Relay relay) {
    log.debug("processing incoming Kind[{}]:{}\n{}",
       incomingFollowSetsEventAsReputationEvent.getKind().getValue(),
       incomingFollowSetsEventAsReputationEvent.getKind().getName().toUpperCase(),
       incomingFollowSetsEventAsReputationEvent.createPrettyPrintJson());

    FollowSetsEvent materializedIncomingFollowSetsEvent =
       cacheFollowSetsEventServiceIF.materialize(incomingFollowSetsEventAsReputationEvent).orElseThrow();

    log.debug("(0ofY) ... materializedIncomingFollowSetsEvent:\n{}", materializedIncomingFollowSetsEvent.createPrettyPrintJson());

    List<BadgeAwardReputationEvent> existingBadgeAwardReputationEvents =
       cacheFollowSetsEventServiceIF.getBadgeAwardReputationEvents(materializedIncomingFollowSetsEvent);

    Map<AddressTag, BadgeAwardReputationEvent> existingBadgeAwardReputationEventsByDefinition =
       existingBadgeAwardReputationEvents.stream()
          .collect(Collectors.toMap(
             UniqueAddressTagEvent::getAddressTag,
             Function.identity(),
             (first, second) ->
                first.getCreatedAt() >= second.getCreatedAt() ? first : second));

    List<BadgeAwardReputationEvent> newReputationEvents =
       materializedIncomingFollowSetsEvent.getBadgeSetsEventList().stream()
          .map(BadgeSetsEvent::getBadgeDefinitionReputationEvent).distinct()
          .map(badgeDefinitionReputationEvent -> {
            BadgeAwardReputationEvent previousReputationEvent =
               Optional.ofNullable(existingBadgeAwardReputationEventsByDefinition.get(
                     badgeDefinitionReputationEvent.asAddressableEventAddressTag()))
                  .orElseGet(() -> createBadgeAwardReputationEvent(
                     materializedIncomingFollowSetsEvent.getAwardRecipientPublicKey(),
                     badgeDefinitionReputationEvent,
                     BigDecimal.ZERO));

            return reputationCalculationServiceIF.calculateReputationEvent(
               materializedIncomingFollowSetsEvent.getAwardRecipientPublicKey(),
               previousReputationEvent,
               badgeDefinitionReputationEvent.getCuratedFormulaEvents(),
               materializedIncomingFollowSetsEvent);
          })
          .toList();

    log.debug("(6ofY) ... newReputationEvent:\n  {}", newReputationEvents.stream().map(EventIF::createPrettyPrintJson));

//    TODO: possibly reverse order below
    existingBadgeAwardReputationEvents.forEach(this::deletePreviousBadgeAwardReputationEvent); // delete old

    List<GenericEventRecord> genericEventRecords = newReputationEvents.stream()
       .map(newReputationEvent ->
          super.processIncomingEvent(newReputationEvent, relay)).flatMap(Optional::stream).toList();

    return genericEventRecords.stream().findFirst();
  }

  private BadgeAwardReputationEvent createBadgeAwardReputationEvent(
     PublicKey badgeReceiverPubkey,
     BadgeDefinitionReputationEvent badgeDefinitionReputationEvent,
     BigDecimal score) {
    return new BadgeAwardReputationEvent(
       aImgIdentity,
       badgeReceiverPubkey,
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEvent,
       score,
       new Relay(afterimageRelayUrl));
  }

  private void deletePreviousBadgeAwardReputationEvent(EventIF previousReputationEvent) {
    cacheServiceIF.deleteEvent(
       new DeletionEvent(
          aImgIdentity,
          List.of(new EventTag(previousReputationEvent.getId(), afterimageRelayUrl)), "aImg delete previous REPUTATION event"));
  }
}
