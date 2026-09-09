package com.prosilion.superconductor.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.SearchRelaysListEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
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
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.util.EventAttributesMap;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static com.prosilion.superconductor.BaseCacheFollowSetsEventServiceIT.getEventIFs;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseBadgeAwardReputationEventMessageListIT extends BaseIntegrationTestDirtiesContextFixtures {
  protected static final List<Kind> ALL_KINDS = List.of(Kind.BADGE_AWARD_EVENT, Kind.FOLLOW_SETS, Kind.BADGE_SETS_EVENT, Kind.CURATION_SETS_BADGE_AWARD_EVENT);
  protected static final List<Filterable> kindFilters =
     List.of(
        new KindFilter(Kind.BADGE_AWARD_EVENT),
        new KindFilter(Kind.FOLLOW_SETS),
        new KindFilter(Kind.BADGE_SETS_EVENT),
        new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT));

  protected final String definitionEventRelayUrl;
  protected final String awardEventRelayUrl;

  protected final List<EventAttributesMap<FormulaEvent>> formulaEventList;
  protected final List<EventAttributesMap<BadgeDefinitionGenericEvent>> badgeDefinitionGenericEventList;
  protected final List<EventAttributesMap<CuratedFormulaEvent>> dbCuratedFormulaEventList;

  abstract protected Relay getAwardEventRelay();

  abstract protected String getDefinitionEventRelayUrl();

  abstract protected Supplier<List<GenericEventRecord>> getAllFxn();
  abstract protected Supplier<List<GenericEventRecord>> getAllDeletedFxn();
  abstract protected void validateResidualDbEventCounts();

  private final Function<List<GenericEventRecord>, List<GenericEventRecord>> getall = genericEventRecordList ->
     genericEventRecordList.stream().filter(event -> ALL_KINDS.contains(event.getKind())).toList();

  BiFunction<List<GenericEventRecord>, Kind, Integer> kindCountFxn = (cacheServiceIFx, kind) ->
     cacheServiceIFx.stream().map(GenericEventRecord::getKind).filter(kind::equals).toList().size();

  protected final BadgeDefinitionReputationEvent badgeDefinitionReputationEvent;
  CacheServiceIF cacheServiceIF;

  protected AbstractBaseBadgeAwardReputationEventMessageListIT(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull String definitionEventRelayUrl,
     @NonNull String awardEventRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(superconductorInstanceIdentity);
    this.cacheServiceIF = cacheServiceIF;

    this.definitionEventRelayUrl = definitionEventRelayUrl;
    this.awardEventRelayUrl = awardEventRelayUrl;

    this.badgeDefinitionGenericEventList = createBadgeDefinitionGenericEventList();
    setupBadgeDefinitionEvents(badgeDefinitionGenericEventList);

    this.formulaEventList = createFormulaEventList();
    this.dbCuratedFormulaEventList = setupFormulaEvents(formulaEventList);

    this.badgeDefinitionReputationEvent = createBadgeDefinitionReputationEvent();

    submitAimgEvent(badgeDefinitionReputationEvent);
  }

  //  TODO: check (potential) overuse of services in console/debug (all 3 tests methods)
  @Test
  void aSuperconductorEventThenAfterimageReq() throws NostrException {
    createAndSubmitSuppliedParameterVoteEvent("1", createUpvoteEventForCanonicalRecipient());
    createAndSubmitSuppliedParameterVoteEvent("1", createUpvoteEventForDifferentRecipient());
    createAndSubmitSuppliedParameterVoteEvent("2", createUpvoteEventForCanonicalRecipient());
    createAndSubmitSuppliedParameterVoteEvent("3", createUpvoteEventForCanonicalRecipient());
    createAndSubmitSuppliedParameterVoteEvent("2", createUpvoteEventForDifferentRecipient());
//
    BadgeAwardCanonicalEvent identicalUpvoteRecipientEvent = createUpvoteEventForCanonicalRecipient();
    createAndSubmitSuppliedParameterVoteEvent("4", identicalUpvoteRecipientEvent);
    createAndSubmitSuppliedParameterVoteEvent("4", identicalUpvoteRecipientEvent);
//
    BadgeAwardCanonicalEvent identicalUpvoteDifferentRecipientEvent = createUpvoteEventForDifferentRecipient();
    createAndSubmitSuppliedParameterVoteEvent("3", identicalUpvoteDifferentRecipientEvent);
    createAndSubmitSuppliedParameterVoteEvent("3", identicalUpvoteDifferentRecipientEvent);
//
    createAndSubmitSuppliedParameterVoteEvent("5", createUpvoteEventForCanonicalRecipient());
    createAndSubmitSuppliedParameterVoteEvent("4", createDownvoteEventForCanonicalRecipient());
//    List<GenericEventRecord> apply = getall.apply(getAllFxn().get());
//
//    assertEquals(2, kindCountFxn.apply(apply, Kind.BADGE_AWARD_EVENT));
//    assertEquals(9, kindCountFxn.apply(apply, Kind.CURATION_SETS_BADGE_AWARD_EVENT));
//    assertEquals(2, kindCountFxn.apply(apply, Kind.FOLLOW_SETS));
//    assertEquals(2, kindCountFxn.apply(apply, Kind.BADGE_SETS_EVENT));
//
//    validateResidualDbEventCounts();
  }

  protected int getEventCountByKindIncludesDeletedEvents(Kind kind) {
    List<GenericEventRecord> genericEventRecords = getAllDeletedFxn().get();
    return genericEventRecords.stream().map(GenericEventRecord::getKind)
       .filter(kind::equals).toList().size();
  }

  protected BadgeAwardCanonicalEvent createUpvoteEventForCanonicalRecipient() {
    PublicKey recipientPublicKey = recipient.getPublicKey();
    log.debug("recipientPublicKey: [{}]", recipientPublicKey.toHexString());
    BadgeAwardCanonicalEvent recipientUpvoteEvent = createUpvoteEvent(new Relay(getDefinitionEventRelayUrl()), recipientPublicKey);
    log.debug("recipientPublicKey id: [{}]", recipientUpvoteEvent.getId());
    return recipientUpvoteEvent;
  }

  protected BadgeAwardCanonicalEvent createDownvoteEventForCanonicalRecipient() {
    return createDownvoteEvent(new Relay(getDefinitionEventRelayUrl()), recipient.getPublicKey());
  }

  protected BadgeAwardCanonicalEvent createUpvoteEventForDifferentRecipient() {
    PublicKey recipientDifferentPublicKey = recipientDifferent.getPublicKey();
    log.debug("recipientDifferentPublicKey: [{}]", recipientDifferentPublicKey.toHexString());
    BadgeAwardCanonicalEvent recipientDifferentUpvoteEvent = createUpvoteEvent(new Relay(getDefinitionEventRelayUrl()), recipientDifferentPublicKey);
    log.debug("recipientDifferentUpvoteEvent id: [{}]", recipientDifferentUpvoteEvent.getId());
    return recipientDifferentUpvoteEvent;
  }

  protected BadgeAwardCanonicalEvent createDownvoteEventForDifferentRecipient() {
    return createDownvoteEvent(new Relay(getDefinitionEventRelayUrl()), recipientDifferent.getPublicKey());
  }

  protected void createAndSubmitSuppliedParameterVoteEvent(String expectedScore, BadgeAwardCanonicalEvent event) {
    EventIF submittedSCVoteEvent = submitSCEvent(
       event,
       definitionEventRelayUrl,
       new Filters(
          new ReferencedPublicKeyFilter(
             new PubKeyTag(event.getAwardRecipientPublicKey())),
          new KindFilter(Kind.BADGE_AWARD_EVENT))).getFirst();

//    BadgeAwardCanonicalEvent reconstructedBadgeAwardGenericEvent = new BadgeAwardCanonicalEvent(
//       submittedSCVoteEvent.asGenericEventRecord(),
//       addressTag -> event.getBadgeDefinitionEvent());

//    CuratedBadgeAwardGenericEvent curatedBadgeAwardEvent = new CuratedBadgeAwardGenericEvent(
//       superconductorInstanceIdentity,
//       reconstructedBadgeAwardGenericEvent,
//       new ReferenceTag(
//          reconstructedBadgeAwardGenericEvent.getRelayTag().orElseThrow().getRelay().getUrl()),
//       new ReferenceTag(
//          reconstructedBadgeAwardGenericEvent.getRelayTag().orElseThrow().getRelay().getUrl()),
//       getAwardEventRelay());

    List<EventIF> eventIFS = submitAfterImageReq(new PubKeyTag(event.getAwardRecipientPublicKey()), awardEventRelayUrl);
    EventIF eventIF = eventIFS.getFirst();

//    EventIF eventIF = submitSCEvent(
//       curatedBadgeAwardEvent,
//       awardEventRelayUrl,
//       new Filters(
//          new ReferencedPublicKeyFilter(
//             new PubKeyTag(event.getAwardRecipientPublicKey())),
//          new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT))).getFirst();

//    List<EventIF> eventIFS = submitAfterImageReq(new PubKeyTag(event.getAwardRecipientPublicKey()), awardEventRelayUrl);

    assertEquals(
       expectedScore,
       eventIF.getContent());
  }

  protected BadgeDefinitionReputationEvent createBadgeDefinitionReputationEvent() {
    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       repDefnCreator,
       superconductorInstanceIdentity.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       getAwardEventRelay(),
       EventAttributesMap.asEventList(this.dbCuratedFormulaEventList));
    return badgeDefinitionReputationEvent;
  }

  protected List<EventAttributesMap<FormulaEvent>> createFormulaEventList() {
    List<EventAttributesMap<FormulaEvent>> eventAttributesMap = EventAttributesMap.asEventAttributesMap(
       List.of(
          createPlusOneFormulaEvent()
          ,
          createMinusOneFormulaEvent()
       ));
    return eventAttributesMap;
  }

  protected List<EventAttributesMap<BadgeDefinitionGenericEvent>> createBadgeDefinitionGenericEventList() {
    return
       EventAttributesMap.asEventAttributesMap(List.of(
          createBadgeAwardUpvoteDefinitionEvent()
          ,
          createBadgeAwardDownvoteDefinitionEvent()
       ));
  }

  protected BadgeAwardCanonicalEvent createUpvoteEvent(Relay relay, PublicKey recipientPublicKey) {
    return new BadgeAwardCanonicalEvent(
       submitter,
       recipientPublicKey,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, upvoteIdentifierTag),
       relay);
  }

  protected BadgeAwardCanonicalEvent createDownvoteEvent(Relay relay, PublicKey recipientPublicKey) {
    return new BadgeAwardCanonicalEvent(
       submitter,
       recipientPublicKey,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, downvoteIdentifierTag),
       relay);
  }

  protected BadgeDefinitionGenericEvent createBadgeAwardUpvoteDefinitionEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       String.format("awardUpvoteDefinitionEvent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       new Relay(getDefinitionEventRelayUrl()));
  }

  protected BadgeDefinitionGenericEvent createBadgeAwardDownvoteDefinitionEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag,
       String.format("awardDownvoteDefinitionEvent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       new Relay(getDefinitionEventRelayUrl()));
  }

  protected BadgeDefinitionGenericEvent createBadgeAwardUpvoteDefinitionDifferentEvent() {
    return new BadgeDefinitionGenericEvent(
       upvoteDefnCreatorDifferent,
       upvoteIdentifierTagDifferent,
       String.format("awardUpvoteDefinitionEventDifferent, definition creator PublicKey: [%s]", upvoteDefnCreator.getPublicKey()),
       new Relay(getDefinitionEventRelayUrl()));
  }

  protected FormulaEvent createPlusOneFormulaEvent() {
    return new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, upvoteIdentifierTag),
       PLUS_ONE_FORMULA,
       new Relay(getAwardEventRelay().getUrl()));
  }

  protected FormulaEvent createMinusOneFormulaEvent() {
    return new FormulaEvent(
       formulaCreator,
       formulaDownvoteIdentifierTag,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, downvoteIdentifierTag),
       MINUS_ONE_FORMULA,
       new Relay(getAwardEventRelay().getUrl()));
  }

  protected FormulaEvent createPlusTenFormulaEvent() {
    return new FormulaEvent(
       formulaCreatorDifferent,
       formulaUpvoteIdentifierTagDifferent,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, upvoteIdentifierTagDifferent),
       PLUS_TEN_FORMULA,
       new Relay(getDefinitionEventRelayUrl()));
  }

  protected List<EventIF> submitSCEvent(BaseEvent event, String url, Filters filters) {
//  submit first Event to superconductor
    submitRelayEventWithDuration_backup(event, url);
//  sanity check event submissions processed by superconductor
    List<BaseMessage> baseMessages = new NostrSingleRequestService().send(
       createSuperconductorReqMessageEvent(generateRandomHex64String(), filters), url
       , Duration.ofSeconds(5));
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

  abstract protected List<EventIF> getReceivedUpvoteCuratedEventIF(BaseEvent event, List<BaseMessage> baseMessages);

  protected void submitRelayEvent(EventIF event, String url) {
    assertEquals(true, new NostrEventPublisher(url).send(new EventMessage(event.asGenericEventRecord()),
       Duration.ofSeconds(5)).getFlag());
  }

  protected void submitRelayEventWithDuration_backup(EventIF event, String url) {
    assertEquals(true, new NostrEventPublisher(url).send(new EventMessage(event.asGenericEventRecord()),
       Duration.ofMinutes(30)).getFlag());
//    TimeUnit.MILLISECONDS.sleep(Duration.ofSeconds(10).toMillis());
  }

  protected void submitAimgEvent(EventIF eventIF) {
    submitRelayEvent(eventIF, awardEventRelayUrl);
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
          recipientPubKeyTag)
//       ,
//       new ExternalIdentityTagFilter(
//          BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG)
    );
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

  abstract protected void validateSetupCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents();

  public void overridableValidateCorrectlyCreatedAndPersistedBadgeDefinitionEventVariants(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    List<EventIF> returnedEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.BADGE_DEFINITION_EVENT))),
          definitionEventRelayUrl));

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
          definitionEventRelayUrl));

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
       new NostrEventPublisher(awardEventRelayUrl)
          .send(
             new EventMessage(formulaEvent.getEvent())
             ,
             Duration.ofSeconds(10)
          ).getFlag()));

    List<EventAttributesMap<CuratedFormulaEvent>> list = getDbCuratedFormulaEventList().stream()
       .map(curatedFormulaEvent ->
          new EventAttributesMap<>(
             curatedFormulaEvent,
             curatedFormulaEvent.getFormulaEventCreatorPublicKey(),
             curatedFormulaEvent.getIdentifierTag())).toList();

    return list;
  }

  public List<CuratedFormulaEvent> getDbCuratedFormulaEventList() {
    List<EventIF> sanityCheckReturnedCuratedFormulaEventIFs = getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          awardEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", sanityCheckReturnedCuratedFormulaEventIFs);

    Set<String> sanityCheckFormulaEventIds = sanityCheckReturnedCuratedFormulaEventIFs.stream()
       .map(eventIF -> eventIF.getTypeSpecificTags(EventTag.class))
       .flatMap(Collection::stream)
       .map(EventTag::getEventId)
       .collect(Collectors.toSet());

    assertTrue(sanityCheckFormulaEventIds.stream().anyMatch(
       EventAttributesMap.asEventList(formulaEventList).stream().map(FormulaEvent::getId)
          .toList()::contains));

    return sanityCheckReturnedCuratedFormulaEventIFs.stream().map(eventIF ->
       new CuratedFormulaEvent(eventIF.asGenericEventRecord())).toList();
  }

  private void setupBadgeDefinitionEvents(List<EventAttributesMap<BadgeDefinitionGenericEvent>> badgeDefinitionGenericEventList) {
    List<BadgeDefinitionGenericEvent> eventList = EventAttributesMap.asEventList(badgeDefinitionGenericEventList);
    eventList
       .forEach(badgeDefinitionGenericEvent ->
          assertTrue(
             new NostrEventPublisher(definitionEventRelayUrl)
                .send(
                   new EventMessage(badgeDefinitionGenericEvent)).getFlag()));

    validateSetupCorrectlyCreatedAndPersistedCurationSetsBadgeDefinitionEvents();
  }
}
