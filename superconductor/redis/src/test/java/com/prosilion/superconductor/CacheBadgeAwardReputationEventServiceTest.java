package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.award.CacheBadgeAwardReputationEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.PLUS_ONE_FORMULA;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.aImgIdentity;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.formulaCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.recipient;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.repDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.reputationIdentifierTag;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheBadgeAwardReputationEventServiceTest
   extends CacheServiceTestFixture<BadgeAwardReputationEvent> {
  @Mock
  CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  @Mock
  CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  @Mock
  CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;

  BadgeDefinitionReputationEvent badgeDefinitionReputationEvent;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardReputationEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardReputationEventService(
       cacheServiceIF,
       null,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       null));
    assertThrows(NullPointerException.class, () -> new CacheBadgeAwardReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheKindAddressTagServiceIF));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheBadgeAwardReputationEventService service = createService();

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
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual = service.getEvent(eventId, relay);

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
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual = service.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagServiceIF, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetEventReturnsEmptyOptionalWhenDefinitionIsMissing() {
    mockLocalGetEventByEventId();
    doReturn(Optional.empty())
       .when(cacheBadgeDefinitionReputationEventServiceIF)
       .getByExpanded(event.getAddressTag());
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual = service.getEvent(eventId, relay);

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetByDirectReturnsReputationAward() {
    AddressTag addressTag = badgeDefinitionReputationEvent.asAddressableEventAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
    mockBadgeDefinition();
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual = service.getByDirect(addressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagServiceIF, Mockito.times(1))
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
  }

  @Test
  void testGetByDirectIgnoresGenericBadgeAward() {
    AddressTag addressTag = badgeDefinitionReputationEvent.asAddressableEventAddressTag();
    BadgeDefinitionGenericEvent genericDefinition =
       new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> genericAward =
       new BadgeAwardGenericEvent<>(
          aImgIdentity, recipient.getPublicKey(), genericDefinition, relay);
    doReturn(List.of(genericAward.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual = service.getByDirect(addressTag);

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testGetByDirectReturnsEmptyOptionalWhenNoAwardExists() {
    AddressTag addressTag = badgeDefinitionReputationEvent.asAddressableEventAddressTag();
    doReturn(List.of())
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(Kind.BADGE_AWARD_EVENT, addressTag);
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual = service.getByDirect(addressTag);

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void testMaterialize() {
    mockBadgeDefinition();
    CacheBadgeAwardReputationEventService service = createService();

    Optional<BadgeAwardReputationEvent> actual =
       service.materialize(event.getGenericEventRecord());

    assertEquals(event, actual.orElseThrow());
    assertEquals(badgeDefinitionReputationEvent, actual.orElseThrow().getBadgeDefinitionEvent());
    verify(cacheBadgeDefinitionReputationEventServiceIF, Mockito.times(1))
       .getByExpanded(event.getAddressTag());
  }

  @Test
  void testMaterializeReturnsEmptyOptionalWhenDefinitionIsMissing() {
    doReturn(Optional.empty())
       .when(cacheBadgeDefinitionReputationEventServiceIF)
       .getByExpanded(event.getAddressTag());

    Optional<BadgeAwardReputationEvent> actual =
       createService().materialize(event.getGenericEventRecord());

    assertEquals(Optional.empty(), actual);
  }

  @SneakyThrows
  @Override
  BadgeAwardReputationEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent =
       new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    FormulaEvent formulaEvent = new FormulaEvent(
       formulaCreator,
       formulaUpvoteIdentifierTag,
       relay,
       badgeDefinitionGenericEvent,
       PLUS_ONE_FORMULA);
    this.badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       relay,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       formulaEvent);
    return new BadgeAwardReputationEvent(
       aImgIdentity,
       recipient.getPublicKey(),
       BADGE_AWARD_REPUTATION_EXTERNAL_IDENTITY_TAG,
       badgeDefinitionReputationEvent,
       BigDecimal.ZERO,
       relay);
  }

  private CacheBadgeAwardReputationEventService createService() {
    return new CacheBadgeAwardReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheKindAddressTagServiceIF);
  }

  private void mockBadgeDefinition() {
    doReturn(Optional.of(badgeDefinitionReputationEvent))
       .when(cacheBadgeDefinitionReputationEventServiceIF)
       .getByExpanded(event.getAddressTag());
  }
}
