package com.prosilion.superconductor.base.curated.supplier;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class AbstractBaseCacheCuratedFormulaEventMessageIT extends BaseIntegrationTestFixtures {
  protected final String formulaEventRelayUrl;
  protected final Relay formulaEventRelay;
  protected final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEventWithRelayTag;
  protected final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEventWithoutRelayTag;

  private final FormulaEvent formulaUpvoteEventWithRelayTag;
  private final FormulaEvent formulaDownvoteEventWithoutRelayTag;

  public AbstractBaseCacheCuratedFormulaEventMessageIT(
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.formulaEventRelayUrl = superconductorRelayUrl;
    this.formulaEventRelay = new Relay(superconductorRelayUrl);

    this.badgeDefinitionUpvoteEventWithRelayTag = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       upvoteIdentifierTag,
       formulaEventRelay);
    setupBadgeDefinitionEvent(badgeDefinitionUpvoteEventWithRelayTag);
    this.badgeDefinitionDownvoteEventWithoutRelayTag = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator,
       downvoteIdentifierTag);
    setupBadgeDefinitionEvent(badgeDefinitionDownvoteEventWithoutRelayTag);

    this.formulaUpvoteEventWithRelayTag = createFormulaEventContainingRelayTag();
    this.formulaDownvoteEventWithoutRelayTag = createFormulaEventWithoutRelayTag();

    setupFormulaEvent(formulaUpvoteEventWithRelayTag);
    setupFormulaEvent(formulaDownvoteEventWithoutRelayTag);
  }

  abstract protected FormulaEvent createFormulaEventContainingRelayTag();
  abstract protected FormulaEvent createFormulaEventWithoutRelayTag();

  @Test
  void testExpectedEvent() throws NostrException {
    List<EventIF> returnedCuratedFormulaEvents = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedCuratedFormulaEvents);

    List<String> eventIds = returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).toList();

    assertTrue(eventIds.contains(formulaUpvoteEventWithRelayTag.getId()));
    assertTrue(eventIds.contains(formulaDownvoteEventWithoutRelayTag.getId()));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(PubKeyTag.class)).map(PubKeyTag::getPublicKey)
       .allMatch(formulaCreator.getPublicKey()::equals));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(IdentifierTag.class))
       .anyMatch(formulaUpvoteIdentifierTag::equals));
    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(IdentifierTag.class))
       .anyMatch(formulaDownvoteIdentifierTag::equals));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class)).map(EventTag::eventId)
       .anyMatch(formulaUpvoteEventWithRelayTag.getId()::equals));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class)).map(EventTag::eventId)
       .anyMatch(formulaDownvoteEventWithoutRelayTag.getId()::equals));

    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(GenericEventRecord::getContent)
       .anyMatch(formulaUpvoteEventWithRelayTag.getContent()::equals));
    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(GenericEventRecord::getContent)
       .anyMatch(formulaDownvoteEventWithoutRelayTag.getContent()::equals));
  }

  private void setupFormulaEvent(FormulaEvent formulaEvent) {
    NostrEventPublisher publisher = new NostrEventPublisher(formulaEventRelayUrl);
    EventMessage eventMessage = new EventMessage(formulaEvent);
    assertTrue(
       publisher
          .send(
             eventMessage)
          .getFlag());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().map(event ->
       event.requireFirstTag(EventTag.class)).map(EventTag::eventId).anyMatch(formulaEvent.getId()::equals));
  }

  private void setupBadgeDefinitionEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    NostrEventPublisher definitionEventNostrEventPublisher = new NostrEventPublisher(formulaEventRelayUrl);
    EventMessage eventMessageBadgeDefinitionUpvoteEventWithRelayTag = new EventMessage(badgeDefinitionGenericEvent);
    assertTrue(
       definitionEventNostrEventPublisher
          .send(
             eventMessageBadgeDefinitionUpvoteEventWithRelayTag)
          .getFlag());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(
                   Kind.BADGE_DEFINITION_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().map(EventIF::getId).anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }
}
