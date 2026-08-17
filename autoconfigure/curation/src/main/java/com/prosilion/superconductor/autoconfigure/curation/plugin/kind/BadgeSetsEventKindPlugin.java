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
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.cache.event.plugin.kind.type.DeleteEventKindPlugin;
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
  private final CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;
  private final DeleteEventKindPlugin deleteEventKindPlugin;

  public BadgeSetsEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF,
     @NonNull CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF,
     @NonNull DeleteEventKindPlugin deleteEventKindPlugin,
     @NonNull EventPlugin eventPlugin) {
    super(eventPlugin);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
    this.cacheCuratedBadgeAwardGenericEventServiceIF = cacheCuratedBadgeAwardGenericEventServiceIF;
    this.deleteEventKindPlugin = deleteEventKindPlugin;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    Optional<BadgeSetsEvent> exists = cacheBadgeSetsEventServiceIF.getEvent(event.getId(), event.getRelayTag().map(RelayTag::getRelay).orElseThrow());
    if (exists.isPresent()) return exists.map(BadgeSetsEvent::getGenericEventRecord);

    PubKeyTag recipientPubKeyTag = event.requireFirstTag(PubKeyTag.class);
    AddressTag badgeDefinitionReputationEventAsAddressTag = event.requireFirstTag(AddressTag.class);

    Optional<BadgeSetsEvent> existingBadgeSetsEvent = cacheBadgeSetsEventServiceIF.getBy(recipientPubKeyTag, badgeDefinitionReputationEventAsAddressTag);

    BadgeSetsEvent materializedBadgeSetsEvent = cacheBadgeSetsEventServiceIF.materialize(event).orElseThrow();

    List<CuratedBadgeAwardGenericEvent> existingCuratedBadgeAwardGenericEvents =
       existingBadgeSetsEvent.stream()
          .map(BadgeSetsEvent::getCuratedBadgeAwardGenericEventList).flatMap(Collection::stream).toList();

    List<String> incomingBadgeSetsEventCuratedUpvoteEventIds = event.getTypeSpecificTags(EventTag.class).stream().map(EventTag::eventId).toList();

    List<String> newUniqueCuratedBadgeAwardGenericEventIds = incomingBadgeSetsEventCuratedUpvoteEventIds.stream().filter(eventId ->
       !existingCuratedBadgeAwardGenericEvents.stream().map(BaseEvent::getId).toList().contains(eventId)).toList();

    List<CuratedBadgeAwardGenericEvent> newCuratedBadgeAwardGenericEventList =
       newUniqueCuratedBadgeAwardGenericEventIds.stream()
          .map(newUniqueCuratedBadgeAwardGenericEventId ->
             cacheCuratedBadgeAwardGenericEventServiceIF.getEvent(
                newUniqueCuratedBadgeAwardGenericEventId,
                event.requireFirstTag(RelayTag.class).getRelay())).flatMap(Optional::stream).toList();

    ArrayList<CuratedBadgeAwardGenericEvent> updatedCuratedBadgeAwardGenericEventList = new ArrayList<>();
    updatedCuratedBadgeAwardGenericEventList.addAll(existingCuratedBadgeAwardGenericEvents);
    updatedCuratedBadgeAwardGenericEventList.addAll(newCuratedBadgeAwardGenericEventList);

    BadgeSetsEvent badgeSetsEvent = new BadgeSetsEvent(
       superconductorInstanceIdentity,
       materializedBadgeSetsEvent.getBadgeDefinitionReputationEvent(),
       updatedCuratedBadgeAwardGenericEventList,
       new Relay(superconductorRelayUrl));

    Optional<GenericEventRecord> genericEventRecord = super.processIncomingEvent(badgeSetsEvent, fromRelay);
    existingBadgeSetsEvent.map(existing -> deleteEventKindPlugin.processIncomingEvent(existing, fromRelay));
    return genericEventRecord;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_SETS_EVENT;
  }
}
