//package com.prosilion.superconductor.autoconfigure.base.service;
//
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BadgeAwardReputationEvent;
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.TagMappedEventIF;
//import com.prosilion.nostr.filter.Filterable;
//import com.prosilion.nostr.tag.AddressTag;
//import com.prosilion.nostr.tag.PubKeyTag;
//import com.prosilion.nostr.user.NostrCheckedException;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheAbstractTagEventService;
//import com.prosilion.superconductor.autoconfigure.base.service.CacheBadgeDefinitionReputationEventService;
//import com.prosilion.superconductor.base.service.event.CacheServiceIF;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.lang.NonNull;
//
//@Slf4j
//public class CacheBadgeAwardReputationEventService extends CacheAbstractTagEventService<AddressTag> {
//  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;
//
//  public CacheBadgeAwardReputationEventService(
//      @NonNull @Qualifier("redisCacheService") CacheServiceIF cacheServiceIF,
//      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
//    super(cacheServiceIF, AddressTag.class);
//    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
//  }
//
//  @Override
//  public void save(EventIF badgeAwardReputationEvent) throws NostrCheckedException {
//    log.info("saving BadgeAwardReputationEvent event with eventId [{}] ...", badgeAwardReputationEvent.getId());
//    super.save(badgeAwardReputationEvent);
//    log.info("...done");
//  }
//
//  public BadgeAwardReputationEvent populate(
//      @NonNull GenericEventRecord baseEvent,
//      @NonNull List<GenericEventRecord> unpopulatedBadgeDefinitionReputationEvents) {
//
//    List<BadgeDefinitionReputationEvent> populatedBadgeDefinitionReputationEvents = unpopulatedBadgeDefinitionReputationEvents.stream()
//        .map(genericEventRecord ->
//            cacheBadgeDefinitionReputationEventService.populate(
//                genericEventRecord,
//                getDefinitionReputationEvent(genericEventRecord)
//                    .map(BadgeDefinitionReputationEvent::getGenericEventRecord)
//                    .stream().toList())).toList();
//
//
//    Function<AddressTag, BadgeDefinitionAwardEvent> fxn = addressTag ->
//        createTagMappedEvent(populatedBadgeDefinitionReputationEvents.stream().filter(genericEventRecord ->
//                Filterable.getTypeSpecificTags(AddressTag.class, genericEventRecord)
//                    .contains(addressTag))
//            .findFirst().orElseThrow().getGenericEventRecord());
//
//    BadgeAwardReputationEvent eventGivenMappedEventTagEvents = createEventGivenMappedTag(
//        baseEvent,
//        BadgeAwardReputationEvent.class,
//        fxn);
//
//    return eventGivenMappedEventTagEvents;
//  }
//
//  private Optional<BadgeDefinitionReputationEvent> getDefinitionReputationEvent(GenericEventRecord genericEventRecord) {
//    Optional<? extends TagMappedEventIF> eventByEventId = super.getEvent(genericEventRecord.getId());
//    Optional<BadgeDefinitionReputationEvent> eventByEventId1 = (Optional<BadgeDefinitionReputationEvent>) eventByEventId;
//    return eventByEventId1;
//  }
//
//  private BadgeDefinitionReputationEvent createTagMappedEvent(GenericEventRecord genericEventRecord) {
//    return super.createBaseEvent(
//        genericEventRecord,
//        BadgeDefinitionReputationEvent.class);
//  }
//
//  public Optional<BadgeAwardReputationEvent> getBadgeAwardReputationEventByEventId(@NonNull String eventId) {
//    Optional<? extends TagMappedEventIF> eventByEventId = super.getEvent(eventId);
//    return (Optional<BadgeAwardReputationEvent>) eventByEventId;
//  }
//
//  public Optional<BadgeAwardReputationEvent> getBadgeAwardReputationEvent(BadgeAwardReputationEvent badgeAwardReputationEvent) {
//    PublicKey badgeReceiverPubkey =
//        badgeAwardReputationEvent.getTags().stream()
//            .filter(PubKeyTag.class::isInstance)
//            .map(PubKeyTag.class::cast)
//            .map(PubKeyTag::getPublicKey)
//            .findFirst().orElseThrow();
//    AddressTag addressTag =
//        badgeAwardReputationEvent.getTags().stream()
//            .filter(AddressTag.class::isInstance)
//            .map(AddressTag.class::cast)
//            .findFirst().orElseThrow();
//
////   List<BadgeAwardReputationEvent> eventsByKindAndPubKeyTagAndAddressTag = (List<BadgeAwardReputationEvent>) super.getEventsByKindAndPubKeyTagAndAddressTag(
////        getKind(),
////        badgeReceiverPubkey,
////        addressTag);
//
////    Optional<? extends TagMappedEventIF> eventByEventId1 = cacheBadgeDefinitionReputationEventService.getEventByEventId(badgeAwardReputationEvent.getId());
////    GenericEventRecord genericEventRecord = new GenericEventRecord();
////    new BadgeAwardReputationEvent(genericEventRecord);
////    Optional<BadgeAwardReputationEvent> eventsByKindAndPubKeyTagAndAddressTag = (Optional<BadgeAwardReputationEvent>) eventByEventId;
//
//    assert (false);
//    return null;
//  }
//
//  @Override
//  public Kind getKind() {
//    return Kind.BADGE_AWARD_EVENT;
//  }
//}
