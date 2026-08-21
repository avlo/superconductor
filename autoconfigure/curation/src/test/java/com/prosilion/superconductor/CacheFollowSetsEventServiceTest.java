package com.prosilion.superconductor;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheFollowSetsEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestDirtiesContextFixtures;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardReputationEventServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheFollowSetsEventServiceTest extends CacheServiceTestFixture<FollowSetsEvent> {
  @Mock
  CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  @Mock
  CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF;
  @Mock
  CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  @Mock
  CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;

  BadgeSetsEvent badgeSetsEvent;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       null,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       null,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.getEvent(eventId, null));
  }

  @Test
  void testGetBadgeAwardReputationEventsRejectsNullFollowSetsEvent() {
    assertThrows(NullPointerException.class, () ->
       createService().getBadgeAwardReputationEvents(null));
  }

  @Test
  void testGetByPubKeyTagRejectsNullPubKeyTag() {
    assertThrows(NullPointerException.class, () -> createService().getBy((PubKeyTag) null));
  }

  @Test
  void testGetByDirectRejectsNullEventTag() {
    assertThrows(NullPointerException.class, () -> createService().getByDirect(null));
  }

  @Test
  void testGetEventByEventId() {
    mockBadgeSetsEvent();
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagServiceIF)
       .getEvent(eventId, relay);
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    Optional<FollowSetsEvent> actual = cacheFollowSetsEventService.getEvent(eventId, relay);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagServiceIF, Mockito.times(1)).getEvent(eventId, relay);
    verify(cacheBadgeSetsEventServiceIF, Mockito.times(1)).getEvent(badgeSetsEvent.getId(), relay);
  }

  @Test
  void testEmptyBadgeSetsThrowsException() {
    mockEmptyBadgeSetsEvent();
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagServiceIF)
       .getEvent(eventId, relay);
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    assertEquals("eventTags.size [1] != badgeSetsEvent.size [0]",
       assertThrows(NostrException.class, () ->
          cacheFollowSetsEventService.getEvent(eventId, relay)).getMessage());
  }

  @Test
  void testGetByPubKeyTag() {
    mockBadgeSetsEvent();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTag(event.getKind(), pubKeyTag);
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    List<FollowSetsEvent> actual = cacheFollowSetsEventService.getBy(pubKeyTag);

    assertEquals(List.of(eventId), actual.stream().map(FollowSetsEvent::getId).toList());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTag(event.getKind(), pubKeyTag);
    verify(cacheBadgeSetsEventServiceIF, Mockito.times(1)).getEvent(badgeSetsEvent.getId(), relay);
  }

  @Test
  void testGetByDirectEventTag() {
    mockBadgeSetsEvent();
    EventTag eventTag = event.getTypeSpecificTags(EventTag.class).getFirst();
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getFirstEventByKindAndEventTag(event.getKind(), eventTag);
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    Optional<FollowSetsEvent> actual = cacheFollowSetsEventService.getByDirect(eventTag);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getFirstEventByKindAndEventTag(event.getKind(), eventTag);
    verify(cacheBadgeSetsEventServiceIF, Mockito.times(1)).getEvent(badgeSetsEvent.getId(), relay);
  }

  @Test
  void testGetBadgeAwardReputationEventsReturnsEmptyListWhenNoAwardsExist() {
    AddressTag reputationDefinitionAddressTag =
       badgeSetsEvent.getBadgeDefinitionReputationEvent().asAddressableEventAddressTag();
    PubKeyTag recipientTag = new PubKeyTag(event.getAwardRecipientPublicKey());
    doReturn(List.of())
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, recipientTag, reputationDefinitionAddressTag);
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    List<BadgeAwardReputationEvent> actual =
       cacheFollowSetsEventService.getBadgeAwardReputationEvents(event);

    assertEquals(List.of(), actual);
    verify(cacheKindAddressTagServiceIF, Mockito.times(1)).getByDirect(
       Kind.BADGE_AWARD_EVENT, recipientTag, reputationDefinitionAddressTag);
  }

  @Test
  void testGetBadgeAwardReputationEventsResolvesMatchingAwards() {
    AddressTag reputationDefinitionAddressTag =
       badgeSetsEvent.getBadgeDefinitionReputationEvent().asAddressableEventAddressTag();
    PubKeyTag recipientTag = new PubKeyTag(event.getAwardRecipientPublicKey());
    BadgeAwardReputationEvent badgeAwardReputationEvent = Mockito.mock(BadgeAwardReputationEvent.class);
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, recipientTag, reputationDefinitionAddressTag);
    doReturn(Optional.of(badgeAwardReputationEvent))
       .when(cacheBadgeAwardReputationEventServiceIF)
       .getEvent(eventId, relay);
    CacheFollowSetsEventService cacheFollowSetsEventService = createService();

    List<BadgeAwardReputationEvent> actual =
       cacheFollowSetsEventService.getBadgeAwardReputationEvents(event);

    assertEquals(List.of(badgeAwardReputationEvent), actual);
    verify(cacheBadgeAwardReputationEventServiceIF, Mockito.times(1)).getEvent(eventId, relay);
  }

  @SneakyThrows
  @Override
  protected FollowSetsEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);

    CuratedFormulaEvent formulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, badgeDefinitionEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent =
       new BadgeDefinitionReputationEvent(
          aImgIdentity,
          repDefnCreator.getPublicKey(),
          reputationIdentifierTag,
          BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
          relay,
          formulaEvent);
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEvent = new BadgeAwardGenericEvent<>(
       submitter, recipient.getPublicKey(), badgeDefinitionEvent, relay);
    CuratedBadgeAwardGenericEvent curatedBadgeAwardEvent = new CuratedBadgeAwardGenericEvent(
       aImgIdentity,
       badgeAwardEvent,
       new ReferenceTag(relay.getUrl()),
       new ReferenceTag(relay.getUrl()),
       relay);
    this.badgeSetsEvent = new BadgeSetsEvent(
       aImgIdentity, badgeDefinitionReputationEvent, curatedBadgeAwardEvent, relay);
    return new FollowSetsEvent(aImgIdentity, badgeSetsEvent, relay);
  }

  private CacheFollowSetsEventService createService() {
    return new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF);
  }

  private void mockBadgeSetsEvent() {
    doReturn(Optional.of(badgeSetsEvent))
       .when(cacheBadgeSetsEventServiceIF)
       .getEvent(badgeSetsEvent.getId(), relay);
  }

  private void mockEmptyBadgeSetsEvent() {
    doReturn(Optional.empty())
       .when(cacheBadgeSetsEventServiceIF)
       .getEvent(badgeSetsEvent.getId(), relay);
  }
}
