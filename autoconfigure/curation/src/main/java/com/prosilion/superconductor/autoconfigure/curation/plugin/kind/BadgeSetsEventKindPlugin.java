package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BadgeSetsEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;
  private final String superconductorRelayUrl;
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;
  private final CacheTagMappedEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;
  private final DeleteEventServiceIF deleteEventServiceIF;
  private final CacheServiceIF cacheServiceIF;

  public BadgeSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull CacheTagMappedEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull EventPlugin eventPlugin,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(eventPlugin);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
    this.deleteEventServiceIF = deleteEventServiceIF;
    this.cacheServiceIF = cacheServiceIF;
  }

  //  TODO: examine re-arch of EventPluginIF such that processIncomingEvent(...) returns BadgeSetsEvent instead of GenericEventRecord  
  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingBadgeSetsEvent, @NonNull Relay fromRelay) {
    log.info("processing incoming badgeSetsEvent\n{}", incomingBadgeSetsEvent.createPrettyPrintJson());

    if (cacheServiceIF.getEventByEventId(incomingBadgeSetsEvent.getId()).isPresent()) {
      log.info("return already existing identical incomingBadgeSetsEvent");
      return Optional.of(incomingBadgeSetsEvent.asGenericEventRecord());
    }

    PubKeyTag recipientPubKeyTag = incomingBadgeSetsEvent.requireFirstTag(PubKeyTag.class);
    AddressTag badgeDefinitionReputationEventAsAddressTag = incomingBadgeSetsEvent.requireFirstTag(AddressTag.class);

    Optional<BadgeSetsEvent> existingBadgeSetsEventOpt = cacheBadgeSetsEventServiceIF.getBy(recipientPubKeyTag, badgeDefinitionReputationEventAsAddressTag);

    if (existingBadgeSetsEventOpt.isPresent()) {
      if (existingBadgeSetsEventOpt.get().equalsSoft(incomingBadgeSetsEvent.asGenericEventRecord())) {
        log.info("return already existing softEquals existingBadgeSetsEvent");
        return existingBadgeSetsEventOpt.map(BaseEvent::asGenericEventRecord);
      }
    }

    BadgeSetsEvent materializedIncomingBadgeSetsEvent = cacheBadgeSetsEventServiceIF.materialize(incomingBadgeSetsEvent).orElseThrow();

    List<CuratedBadgeAwardGenericEvent> existingCuratedBadgeAwardGenericEvents = existingBadgeSetsEventOpt.stream().map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList).flatMap(Collection::stream).toList();

    List<String> incomingBadgeSetsEventCuratedUpvoteEventIds = incomingBadgeSetsEvent.getTypeSpecificTags(EventTag.class).stream().map(EventTag::eventId).toList();

    List<String> newUniqueCuratedBadgeAwardGenericEventIds = incomingBadgeSetsEventCuratedUpvoteEventIds.stream().filter(eventId -> !existingCuratedBadgeAwardGenericEvents.stream().map(BaseEvent::getId).toList().contains(eventId)).toList();

    List<CuratedBadgeAwardGenericEvent> newCuratedBadgeAwardGenericEventList = newUniqueCuratedBadgeAwardGenericEventIds.stream().map(newUniqueCuratedBadgeAwardGenericEventId -> cacheCuratedBadgeAwardGenericEventServiceIF.getEvent(newUniqueCuratedBadgeAwardGenericEventId, incomingBadgeSetsEvent.requireFirstTag(RelayTag.class).getRelay())).flatMap(Optional::stream).toList();

    ArrayList<CuratedBadgeAwardGenericEvent> updatedCuratedBadgeAwardGenericEventList = new ArrayList<>();
    updatedCuratedBadgeAwardGenericEventList.addAll(existingCuratedBadgeAwardGenericEvents);
    updatedCuratedBadgeAwardGenericEventList.addAll(newCuratedBadgeAwardGenericEventList);

    BadgeSetsEvent newBadgeSetsEvent = materializedIncomingBadgeSetsEvent.createNewFromExisting(superconductorInstanceIdentity, updatedCuratedBadgeAwardGenericEventList);

//    below saves and then deletes same badgesetsevent
//    existingBadgeSetsEventOpt.ifPresent(this::checkDelete);
    Optional<GenericEventRecord> genericEventRecord = super.processIncomingEvent(newBadgeSetsEvent, fromRelay);
    return genericEventRecord;
  }

  public void checkDelete(BadgeSetsEvent previousBadgeSetsEvent) {
    deleteEventServiceIF.processIncomingEvent(previousBadgeSetsEvent, new Relay(superconductorRelayUrl));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
