package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class CuratedBadgeAwardGenericEventKindPlugin extends PublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;
  private final Relay superconductorRelay;
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;

  public CuratedBadgeAwardGenericEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull NotifierService notifierService,
     @NonNull EventPluginIF eventPluginIF) {
    super(notifierService, eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
    this.superconductorRelay = new Relay(superconductorRelayUrl);
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    log.debug("processIncomingEvent(event, fromRelay) [{}]...\n{}", fromRelay.getUrl(), event.createPrettyPrintJson());

    Optional<RelayTag> relayTag = event.findFirstTag(RelayTag.class);
    AddressTag suppliedAddressTag = event.requireFirstTag(AddressTag.class);

    //  super.processIncomingEvent(event, fromRelay);  save incoming BadgeAwardGenericEvent

//    TODO: investigate move below (and dependent functions) to CacheCuratedBadgeDefinitionGenericEventService
    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent =
       attempt_1of2_AvailableServiceRequests(
          suppliedAddressTag.findRelay()
             .map(relay ->
                addressTag_1stOf3_FormatOptions(relay, suppliedAddressTag))
             .or(() -> relayTag.map(rTag ->
                addressTag_2ndOf3_FormatOptions(rTag, suppliedAddressTag)))
             .orElseGet(() ->
                addressTag_3rdOf3_FormatOptions(suppliedAddressTag)))
          .or(() ->
             attempt_2of2_AvailableServiceRequests(
                new AddressTag(
                   suppliedAddressTag.getKind(),
                   suppliedAddressTag.getPublicKey(),
                   suppliedAddressTag.getIdentifierTag(),
                   fromRelay)));

    if (curatedBadgeDefinitionGenericEvent.isEmpty()) {
      log.debug("non-existent curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent).  return Optional.empty()");
      return Optional.empty();
    }

    log.debug("found existing curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent):\n{}\nre-composing BadgeAwardGenericEvent...",
       curatedBadgeDefinitionGenericEvent.get().createPrettyPrintJson());
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardGenericEvent =
       new BadgeAwardGenericEvent<>(
          event.asGenericEventRecord(),
//          curatedBadgeDefinitionGenericEvent.get().asGenericEventRecord(),
          aTag -> curatedBadgeDefinitionGenericEvent.get().getBadgeDefinitionGenericEvent());
    log.debug("...done:\n{}", badgeAwardGenericEvent.createPrettyPrintJson());

    log.debug("composing new CuratedBadgeAwardGenericEvent...");
    String guaranteedSourceRelayUrl = relayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse(fromRelay.getUrl());
    CuratedBadgeAwardGenericEvent curatedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       superconductorInstanceIdentity,
       badgeAwardGenericEvent,
       curatedBadgeDefinitionGenericEvent.get(),
       new ReferenceTag(guaranteedSourceRelayUrl),
       superconductorRelay);
    log.debug("...done:\n{}", curatedBadgeAwardGenericEvent.createPrettyPrintJson());

    log.debug("saving CuratedBadgeAwardGenericEvent with guaranteedSourceRelayUrl as ReferenceTag URL: [{}]", guaranteedSourceRelayUrl);
    return super.processIncomingEvent(curatedBadgeAwardGenericEvent, superconductorRelay);
  }

  private AddressTag addressTag_1stOf3_FormatOptions(Relay relay, AddressTag suppliedAddressTag) {
    log.debug("supplied AddressTag has a relay, use addressTag_1stOf3_FormatOptions(AddressTag suppliedAddressTag).  addressTag:\n{}", suppliedAddressTag.toStringPrettyPrint());
    AddressTag addressTag = new AddressTag(
       suppliedAddressTag.getKind(),
       suppliedAddressTag.getPublicKey(),
       suppliedAddressTag.getIdentifierTag(),
       relay);
    return addressTag;
  }

  private AddressTag addressTag_2ndOf3_FormatOptions(RelayTag relayTag, AddressTag suppliedAddressTag) {
    log.debug("supplied AddressTag did not have a relay, trying with RelayTag, use addressTag_2ndOf3_FormatOptions(AddressTag suppliedAddressTag).  addressTag:\n{}", suppliedAddressTag.toStringPrettyPrint());
    return new AddressTag(
       suppliedAddressTag.getKind(),
       suppliedAddressTag.getPublicKey(),
       suppliedAddressTag.getIdentifierTag(),
       relayTag.getRelay());
  }

  private @NonNull AddressTag addressTag_3rdOf3_FormatOptions(AddressTag suppliedAddressTag) {
    log.debug("No relay variant of any kind found, addressTag_3rdOf3_FormatOptions(AddressTag suppliedAddressTag).  addressTag:\n{}", suppliedAddressTag.toStringPrettyPrint());
    AddressTag addressTag = new AddressTag(
       suppliedAddressTag.getKind(),
       suppliedAddressTag.getPublicKey(),
       suppliedAddressTag.getIdentifierTag());
    return addressTag;
  }

  private Optional<CuratedBadgeDefinitionGenericEvent> attempt_1of2_AvailableServiceRequests(AddressTag addressTag) {
    log.debug("inside attempt_1of2_AvailableServiceRequests using (AddressTag addressTag).  addressTag:\n{}",
       addressTag.toStringPrettyPrint());
    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent = cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(addressTag);
    log.debug(curatedBadgeDefinitionGenericEvent.map(BaseEvent::createPrettyPrintJson).orElse(
       "nothing found locally, or with addressTag containing Relay.  returning Optional.empty()"));
    return curatedBadgeDefinitionGenericEvent;
  }

  private Optional<CuratedBadgeDefinitionGenericEvent> attempt_2of2_AvailableServiceRequests(AddressTag addressTagConstructedFromRelayTagIfPresent) {
    log.debug("inside attempt_2of2_AvailableServiceRequests(AddressTag addressTagConstructedFromRelayTagIfPresent).  addressTag:\n{}",
       addressTagConstructedFromRelayTagIfPresent.toStringPrettyPrint());
    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent = cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(addressTagConstructedFromRelayTagIfPresent);
    log.debug(curatedBadgeDefinitionGenericEvent.map(BaseEvent::createPrettyPrintJson).orElse("Optional.empty()"));
    return curatedBadgeDefinitionGenericEvent;
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
