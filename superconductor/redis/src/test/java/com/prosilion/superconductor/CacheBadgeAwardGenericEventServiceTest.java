package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardGenericEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.recipient;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.submitter;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheBadgeAwardGenericEventServiceTest
   extends CacheServiceTestFixture<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> {
  @Mock
  CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  @Mock
  CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;
  @Mock
  CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  BadgeDefinitionGenericEvent badgeDefinitionGenericEvent;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardGenericEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardGenericEventService(
       cacheServiceIF,
       null,
       cacheBadgeDefinitionGenericEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheBadgeAwardGenericEventService service = createService();

    assertThrows(NullPointerException.class, () -> service.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> service.getEvent(eventId, null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createService().getByDirect(null));
  }

  @Test
  void testGetEventByEventIdFromLocalCache() {
    mockLocalGetEventByEventId();
    mockBadgeDefinition();
    CacheBadgeAwardGenericEventService service = createService();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       service.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
  }

  @Test
  void testGetEventByEventIdFromRemoteReferenceService() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagServiceIF)
       .getEvent(eventId, relay);
    mockBadgeDefinition();
    CacheBadgeAwardGenericEventService service = createService();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       service.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagServiceIF, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetEventReturnsEmptyOptionalWhenDefinitionIsMissing() {
    mockLocalGetEventByEventId();
    doReturn(Optional.empty())
       .when(cacheBadgeDefinitionGenericEventServiceIF)
       .getByExpanded(event.getAddressTag());
    doReturn(Optional.empty())
       .when(cacheReferenceEventTagServiceIF)
       .getEvent(eventId, relay);
    CacheBadgeAwardGenericEventService service = createService();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       service.getEvent(eventId, relay);

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetByDirectAddressTag() {
    AddressTag addressTag = badgeDefinitionGenericEvent.asAddressableEventAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
    mockBadgeDefinition();
    CacheBadgeAwardGenericEventService service = createService();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       service.getByDirect(addressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagServiceIF, Mockito.times(1))
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
  }

  @Test
  void testGetByDirectReturnsEmptyOptional() {
    AddressTag addressTag = badgeDefinitionGenericEvent.asAddressableEventAddressTag();
    doReturn(List.of())
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
    CacheBadgeAwardGenericEventService service = createService();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       service.getByDirect(addressTag);

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetByDirectIgnoresReputationBadgeAward() {
    AddressTag addressTag = badgeDefinitionGenericEvent.asAddressableEventAddressTag();
    GenericEventRecord reputationAward = Mockito.mock(GenericEventRecord.class);
    doReturn(Optional.of(Mockito.mock(ExternalIdentityTag.class)))
       .when(reputationAward)
       .findFirstTag(ExternalIdentityTag.class);
    doReturn(List.of(reputationAward, event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
    mockBadgeDefinition();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       createService().getByDirect(addressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
  }

  @Test
  void testMaterialize() {
    mockBadgeDefinition();
    CacheBadgeAwardGenericEventService service = createService();

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       service.materialize(event.getGenericEventRecord());

    assertEquals(event, actual.orElseThrow());
    assertEquals(badgeDefinitionGenericEvent, actual.orElseThrow().getBadgeDefinitionEvent());
    verify(cacheBadgeDefinitionGenericEventServiceIF, Mockito.times(1))
       .getByExpanded(event.getAddressTag());
  }

  @Test
  void testMaterializeReturnsEmptyOptionalWhenDefinitionIsMissing() {
    doReturn(Optional.empty())
       .when(cacheBadgeDefinitionGenericEventServiceIF)
       .getByExpanded(event.getAddressTag());

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> actual =
       createService().materialize(event.getGenericEventRecord());

    assertEquals(Optional.empty(), actual);
  }

  @Override
  BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createEvent() {
    this.badgeDefinitionGenericEvent =
       new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionGenericEvent,
       relay);
  }

  private CacheBadgeAwardGenericEventService createService() {
    return new CacheBadgeAwardGenericEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF,
       cacheKindAddressTagServiceIF);
  }

  private void mockBadgeDefinition() {
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventServiceIF)
       .getByExpanded(event.getAddressTag());
  }
}
