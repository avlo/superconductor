package com.prosilion.superconductor.supplier;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.SearchRelaysListEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.ExternalIdentityTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelaysTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.RequestSubscriber;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.EventAttributesMap;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.prosilion.superconductor.BaseFollowSetsEventServiceIT.getEventIFs;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseBadgeAwardReputationEventMessageListIT extends BaseIntegrationTestFixtures {
  protected final String definitionEventRelayUrl;
  protected final String awardEventRelayUrl;
  protected final String formulaEventRelayUrl;
  protected final Relay formulaEventRelay;

  protected final List<EventAttributesMap<FormulaEvent>> formulaEventList;
  protected final List<EventAttributesMap<BadgeDefinitionGenericEvent>> badgeDefinitionGenericEventList;
  protected final List<EventAttributesMap<CuratedFormulaEvent>> dbCuratedFormulaEventList;

  abstract protected List<EventAttributesMap<FormulaEvent>> createFormulaEventList();
  abstract protected List<EventAttributesMap<BadgeDefinitionGenericEvent>> createBadgeDefinitionGenericEventList();

  protected AbstractBaseBadgeAwardReputationEventMessageListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl) {
    super(superconductorInstanceIdentity);

    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;
    this.formulaEventRelayUrl = definitionEventRelayUrl;
    this.formulaEventRelay = new Relay(definitionEventRelayUrl);

    this.badgeDefinitionGenericEventList = createBadgeDefinitionGenericEventList();
    setupBadgeDefinitionEvents(badgeDefinitionGenericEventList);

    this.formulaEventList = createFormulaEventList();
    this.dbCuratedFormulaEventList = setupFormulaEvents(formulaEventList);

    submitAimgEvent(
       createBadgeDefinitionReputationEvent());
  }

  protected BadgeDefinitionReputationEvent createBadgeDefinitionReputationEvent() {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       superconductorInstanceIdentity.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       new Relay(awardEventRelayUrl),
       EventAttributesMap.asEventList(this.dbCuratedFormulaEventList));
    return badgeDefinitionReputationEvent;
  }

  public List<CuratedFormulaEvent> getDbCuratedFormulaEventList() {
    List<EventIF> returnedCuratedFormulaEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedFormulaEventIFs);

    Set<String> sanityCheckFormulaEventIds = returnedCuratedFormulaEventIFs.stream()
       .map(eventIF -> eventIF.getTypeSpecificTags(EventTag.class))
       .flatMap(Collection::stream)
       .map(EventTag::getEventId)
       .collect(Collectors.toSet());

    assertTrue(sanityCheckFormulaEventIds.stream().anyMatch(
       EventAttributesMap.asEventList(formulaEventList).stream().map(FormulaEvent::getId)
          .toList()::contains));

    return returnedCuratedFormulaEventIFs.stream().map(eventIF ->
       new CuratedFormulaEvent(eventIF.asGenericEventRecord())).toList();
  }

  protected List<EventIF> submitSCEvent(BaseEvent event, String url, Filters filters) {
//  submit first Event to superconductor
    submitRelayEventWithDuration_backup(event, url);
//  sanity check event submissions processed by superconductor
    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(
       createSuperconductorReqMessageEvent(generateRandomHex64String(), filters), url);
    return getReceivedUpvoteCuratedEventIF(event, baseMessages);
  }

  protected List<EventIF> submitSCEventWithDuration_backup(BaseEvent event, String url, Filters filters) {
//  submit first Event to superconductor
    submitRelayEventWithDuration_backup(event, url);
//  sanity check event submissions processed by superconductor
    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(
       createSuperconductorReqMessageEvent(generateRandomHex64String(), filters), url
       , Duration.ofMinutes(30));
    return getReceivedUpvoteCuratedEventIF(event, baseMessages);
  }

  private @NonNull List<EventIF> getReceivedUpvoteCuratedEventIF(BaseEvent event, List<BaseMessage> baseMessages) {
    log.debug("retrieved superconductor events:");
    List<EventIF> receivedEventIFs = getGenericEvents(baseMessages);
    receivedEventIFs.stream().map(EventIF::asGenericEventRecord).map(GenericEventRecord::createPrettyPrintJson).forEach(log::debug);

    assertTrue(receivedEventIFs.stream().map(eventIF ->
       eventIF.requireFirstTag(EventTag.class).getEventId()).anyMatch(event.getId()::contains));

    assertTrue(receivedEventIFs.stream().map(eventIF ->
       eventIF.requireFirstTag(PubKeyTag.class).getPublicKey()).anyMatch(event.requireFirstTag(PubKeyTag.class).getPublicKey()::equals));
    return receivedEventIFs;
  }

  protected void submitRelayEvent(EventIF event, String url) {
    assertEquals(true, new NostrEventPublisher(url).send(new EventMessage(event.asGenericEventRecord())).getFlag());
  }

  protected void submitRelayEventWithDuration_backup(EventIF event, String url) {
    assertEquals(true, new NostrEventPublisher(url).send(new EventMessage(event.asGenericEventRecord()), Duration.ofMinutes(30)).getFlag());
//    TimeUnit.MILLISECONDS.sleep(Duration.ofSeconds(10).toMillis());
  }

  protected void submitAimgEvent(EventIF eventIF) {
    submitRelayEventWithDuration_backup(eventIF, awardEventRelayUrl);
//    TimeUnit.MILLISECONDS.sleep(1000);
  }

  protected void submitAimgEventWithDuration_backup(EventIF eventIF) {
    submitRelayEventWithDuration_backup(eventIF, awardEventRelayUrl);
  }

  protected ReqMessage createSuperconductorReqMessageEvent(String subscriberId, Filters filters) {
    return new ReqMessage(subscriberId, filters);
  }

  protected List<EventIF> submitAfterImageReq(PubKeyTag recipientPubKeyTag, String url) {
    log.debug("query Aimg for badgeAwardUpvoteEvent:");
    List<BaseMessage> subscriber = new NostrSingleRequestService().send(
       createAfterImageReqMessage(
          generateRandomHex64String(),
          recipientPubKeyTag),
       url
       , Duration.ofSeconds(10)
    );

    log.debug("afterimage returned events:");
    return getGenericEvents(subscriber);
  }

  @SneakyThrows
  protected ReqMessage createAfterImageReqMessage(String subscriberId, PubKeyTag recipientPubKeyTag) {
    ReqMessage reqMessage = new ReqMessage(
       subscriberId,
       createBadgeAwardRecipientFilters(recipientPubKeyTag));
    log.debug(Util.prettyFormatJson(reqMessage.encode(), 2));
    return reqMessage;
  }

  protected Filters createBadgeAwardRecipientFilters(PubKeyTag recipientPubKeyTag) {
    return new Filters(
       new KindFilter(
          Kind.BADGE_AWARD_EVENT)
//          new IdentifierTagFilter(
//             new IdentifierTag(defnCreatorPublicKey.toHexString())),
//       new AddressTagFilter(
//          new AddressTag(
//             Kind.BADGE_DEFINITION_EVENT,
//             repDefnCreator.getPublicKey(),
//             reputationIdentifierTag))
       ,
       new ReferencedPublicKeyFilter(
          recipientPubKeyTag),
       new ExternalIdentityTagFilter(
          BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG));
  }

  protected void submitAfterImageReqWithSubscriber(PubKeyTag recipientPubKeyTag, String url, RequestSubscriber<BaseMessage> subscriber) {
    new NostrSingleRequestService().send(
       createAfterImageReqMessage(
          generateRandomHex64String(),
          recipientPubKeyTag),
       url, subscriber);
  }

  protected BaseEvent createSearchRelaysListEventMessage() {
    Util.debug(log, "createSearchRelaysListEventMessage to url:  {}", definitionEventRelayUrl, true, '1');
    return new SearchRelaysListEvent(
       Identity.generateRandomIdentity(),
       new RelaysTag(new Relay(definitionEventRelayUrl)),
       "Search Relays List sent from aImg IT 5556");
  }

  protected List<EventIF> validateGeneralAfterimageRequestResults(List<EventIF> returnedReputationEventIFs) {
    assertFalse(returnedReputationEventIFs.isEmpty());

    assertTrue(returnedReputationEventIFs.stream().anyMatch(eventIF ->
       eventIF.findFirstTag(PubKeyTag.class).map(PubKeyTag::getPublicKey).stream()
          .anyMatch(recipient.getPublicKey()::equals)));

    assertFalse(returnedReputationEventIFs.stream().anyMatch(eventIF ->
       eventIF.findFirstTag(AddressTag.class).stream()
          .filter(addressTag -> addressTag.getKind().equals(Kind.BADGE_DEFINITION_EVENT))
          .filter(addressTag -> addressTag.getPublicKey().equals(repDefnCreator.getPublicKey()))
          .filter(addressTag -> addressTag.requireIdentifierTag().equals(reputationIdentifierTag))
          .toList().isEmpty()));

    assertTrue(returnedReputationEventIFs.stream().anyMatch(eventIF ->
       eventIF.findFirstTag(ExternalIdentityTag.class).stream()
          .anyMatch(this::isEquals)));

    return returnedReputationEventIFs;
  }

  private boolean isEquals(ExternalIdentityTag externalIdentityTag) {
    log.debug("       incoming          externalIdentityTag:\n {}", externalIdentityTag);
    log.debug("BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG:\n {}", BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG);
    boolean equals = externalIdentityTag.equals(BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG);
    log.debug(String.format("  %s", equals ?
       "+++ MATCH" :
       "--- NO MATCH: " + StringUtils.difference(BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG.toString(), externalIdentityTag.toString())));
    return equals;
  }

  protected List<EventIF> validateSpecificAfterimageRequestResults(RequestSubscriber<BaseMessage> subscriber, int count, String expectedScore) {
    return validateSpecificAfterimageRequestResults(
       getGenericEvents(
          subscriber.getItems()),
       count,
       expectedScore);
  }

  protected List<EventIF> validateBadgeAwardProcessingCompletion(List<GenericEventRecord> genericEventRecords, int count) {
    return validateCount(genericEventRecords.stream().collect(Collectors.toUnmodifiableList()), count);
  }

  protected List<EventIF> validateBadgeAwardProcessingCompletion(List<GenericEventRecord> genericEventRecords, int count, String expectedScore) {
    return validateSpecificAfterimageRequestResults(genericEventRecords.stream().collect(Collectors.toUnmodifiableList()), count, expectedScore);
  }

  protected List<EventIF> validateSpecificAfterimageRequestResults(List<EventIF> events, int count, String expectedScore) {
    validateCount(events, count);
    assertEquals(expectedScore, events.getFirst().getContent());
    return events;
  }

  protected List<EventIF> validateCount(List<EventIF> events, int count) {
    assertEquals(count, (long) events.size());
    return events;
  }

  protected List<EventIF> getGenericEvents(List<BaseMessage> messages) {
    return messages.stream()
       .filter(EventMessage.class::isInstance)
       .map(EventMessage.class::cast)
       .map(EventMessage::getEvent)
       .toList();
  }

  public static String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", "");
  }

  public void overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.BADGE_DEFINITION_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> eventIds = returnedEventIFs.stream().map(EventIF::getId).collect(Collectors.toSet());
    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }

  public void overridableValidateCorrectlyCreatedAndPersistedFormulaEventVariant(List<FormulaEvent> formulaEventList) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> sanityCheckFormulaEventIds =
       returnedEventIFs.stream()
          .map(event -> event.requireFirstTag(EventTag.class))
          .map(EventTag::getEventId)
          .collect(Collectors.toSet());

    assertTrue(sanityCheckFormulaEventIds.stream().anyMatch(
       EventAttributesMap.asEventList(this.formulaEventList).stream()
          .map(BaseEvent::getId)
          .toList()::contains));
  }

  private List<EventAttributesMap<CuratedFormulaEvent>> setupFormulaEvents(List<EventAttributesMap<FormulaEvent>> formulaEvents) {
    formulaEvents.forEach(formulaEvent -> assertTrue(
       new NostrEventPublisher(formulaEventRelayUrl)
          .send(
             new EventMessage(formulaEvent.getEvent())).getFlag()));

    return getDbCuratedFormulaEventList().stream()
       .map(curatedFormulaEvent ->
          new EventAttributesMap<>(
             curatedFormulaEvent,
             curatedFormulaEvent.getFormulaEventCreatorPublicKey(),
             curatedFormulaEvent.getIdentifierTag())).toList();
  }

  private void setupBadgeDefinitionEvents(List<EventAttributesMap<BadgeDefinitionGenericEvent>> badgeDefinitionGenericEventList) {
    List<BadgeDefinitionGenericEvent> eventList = EventAttributesMap.asEventList(badgeDefinitionGenericEventList);
    eventList
       .forEach(badgeDefinitionGenericEvent ->
          assertTrue(
             new NostrEventPublisher(definitionEventRelayUrl)
                .send(
                   new EventMessage(badgeDefinitionGenericEvent)).getFlag()));

    validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(eventList);
  }

  protected void validateCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents(List<BadgeDefinitionGenericEvent> badgeDefinitionGenericEventList) {
    List<EventIF> sanityCheckReturnedBadgeDefinitionEvents = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

    log.debug("returned BadgeDefinitionEvents:");
    log.debug("  {}", sanityCheckReturnedBadgeDefinitionEvents);

    Set<String> sanityCheckCurationSetsBadgeDefinitionEventIds =
       sanityCheckReturnedBadgeDefinitionEvents.stream()
          .map(event -> event.requireFirstTag(EventTag.class))
          .map(EventTag::getEventId)
          .collect(Collectors.toSet());

    Predicate<String> contains = EventAttributesMap.asEventList(this.badgeDefinitionGenericEventList).stream().map(EventIF::getId).toList()::contains;

    assertTrue(sanityCheckCurationSetsBadgeDefinitionEventIds.stream().anyMatch(
       contains));
  }

  protected Filters upvoteAndOrDownvoteDefinitionEventFilter =
     new Filters(
        new ReferencedPublicKeyFilter(
           new PubKeyTag(upvoteDefnCreator.getPublicKey())),
        new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT));

  protected BiFunction<PublicKey, AddressTag, Filters> curatedFormulaEventFilter = (publicKey, addressTag) ->
     new Filters(
        new ReferencedPublicKeyFilter(new PubKeyTag(publicKey)),
        new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT),
        new AddressTagFilter(addressTag));
}
