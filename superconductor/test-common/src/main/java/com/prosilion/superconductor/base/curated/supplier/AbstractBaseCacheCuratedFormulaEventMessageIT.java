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
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.TestUtils;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

  protected final CacheServiceIF cacheServiceIF;

  public AbstractBaseCacheCuratedFormulaEventMessageIT(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorInstanceIdentity);
    this.cacheServiceIF = cacheServiceIF;
    this.formulaEventRelayUrl = superconductorRelayUrl;
    this.formulaEventRelay = new Relay(superconductorRelayUrl);

    this.badgeDefinitionUpvoteEventWithRelayTag = createDefinitionEventContainingRelayTag();
    this.badgeDefinitionDownvoteEventWithoutRelayTag = createDefinitionEventWithoutRelayTag();
    setupBadgeDefinitionEvent(badgeDefinitionUpvoteEventWithRelayTag);
    setupBadgeDefinitionEvent(badgeDefinitionDownvoteEventWithoutRelayTag);

    this.formulaUpvoteEventWithRelayTag = createFormulaEventContainingRelayTag();
    this.formulaDownvoteEventWithoutRelayTag = createFormulaEventWithoutRelayTag();
    setupFormulaEvent(formulaUpvoteEventWithRelayTag);
    setupFormulaEvent(formulaDownvoteEventWithoutRelayTag);
  }

  abstract protected BadgeDefinitionGenericEvent createDefinitionEventContainingRelayTag();
  abstract protected BadgeDefinitionGenericEvent createDefinitionEventWithoutRelayTag();

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
       .map(event -> event.requireFirstTag(AddressTag.class))
       .anyMatch(formulaUpvoteEventWithRelayTag.getAddressTag()::equals));
    assertTrue(returnedCuratedFormulaEvents.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(AddressTag.class))
       .anyMatch(formulaDownvoteEventWithoutRelayTag.getAddressTag()::equals));

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
                new KindFilter(Kind.CURATION_SETS_FORMULA_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    assertTrue(returnedEventIFs.stream().map(event ->
       event.requireFirstTag(EventTag.class)).map(EventTag::eventId).anyMatch(formulaEvent.getId()::equals));
  }

  private void setupBadgeDefinitionEvent(BadgeDefinitionGenericEvent badgeDefinitionGenericEvent) {
    assertTrue(
       new NostrEventPublisher(formulaEventRelayUrl)
          .send(
             new EventMessage(badgeDefinitionGenericEvent))
          .getFlag());

    List<EventIF> returnedEventIFs = TestUtils.getEventIFs(
       new NostrSingleRequestService().send(
          new ReqMessage(
             Factory.generateRandomHex64String(),
             new Filters(
                new KindFilter(Kind.CURATION_SETS_BADGE_DEFINITION_EVENT))),
          formulaEventRelayUrl));

    log.debug("returned events:");
    log.debug("  {}", returnedEventIFs);

    Set<String> eventIds = returnedEventIFs.stream().map(EventIF::asGenericEventRecord)
       .map(event -> event.requireFirstTag(EventTag.class))
       .map(EventTag::getEventId).collect(Collectors.toSet());

    assertTrue(eventIds.stream().anyMatch(badgeDefinitionGenericEvent.getId()::equals));
  }
}
