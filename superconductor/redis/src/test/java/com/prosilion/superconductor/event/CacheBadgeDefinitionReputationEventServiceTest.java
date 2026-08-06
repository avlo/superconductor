package com.prosilion.superconductor.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheKindAddressTagService;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
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
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.repDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.reputationIdentifierTag;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteDefnCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheBadgeDefinitionReputationEventServiceTest extends CacheServiceTestFixture<BadgeDefinitionReputationEvent> {
  @Mock
  CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
  @Mock
  CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  @Mock
  CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  @Mock
  CacheKindAddressTagService cacheKindAddressTagService;

  CuratedFormulaEvent curatedFormulaEvent;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       null,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheCuratedFormulaEventServiceIF,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       null,
       cacheKindAddressTagService));
    assertThrows(NullPointerException.class, () -> new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheBadgeDefinitionReputationEventService service = createService();

    assertThrows(NullPointerException.class, () -> service.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> service.getEvent(eventId, null));
  }

  @Test
  void testGetByExpandedRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createService().getByExpanded(null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createService().getByDirect(null));
  }

  private void mockFormulaEvent() {
    doReturn(Optional.of(curatedFormulaEvent)).when(cacheCuratedFormulaEventServiceIF)
       .getByAuthorAndIdentifierTag(
          curatedFormulaEvent.asAddressableEventAddressTag().getPublicKey(),
          curatedFormulaEvent.asAddressableEventAddressTag().getIdentifierTag());
  }

  @Test
  void testGetEventByEventIdFromLocalCache() {
    mockLocalGetEventByEventId();
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual = service.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
  }

  @Test
  void testGetEventByEventIdFromEventTagCacheLookup() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndEventTag(event.getKind(), new EventTag(eventId));
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual = service.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndEventTag(
       event.getKind(), new EventTag(eventId));
  }

  @Test
  void testGetEventByEventIdFromRemoteReferenceService() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndEventTag(
       event.getKind(), new EventTag(eventId));
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagServiceIF)
       .getEvent(eventId, relay);
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual = service.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagServiceIF, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetByExpandedAddressTag() {
    AddressTag addressTag = event.asAddressableEventAddressTag();
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceAddressTagServiceIF)
       .getByExpanded(addressTag);
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual = service.getByExpanded(addressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testGetByPublicKeyAndIdentifierTag() {
    PubKeyTag pubKeyTag = new PubKeyTag(repDefnCreator.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(
          event.getKind(), pubKeyTag, reputationIdentifierTag);
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual =
       service.getBy(pubKeyTag, reputationIdentifierTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       event.getKind(), pubKeyTag, reputationIdentifierTag);
  }

  @Test
  void testGetByPublicKeyAndIdentifierTagIgnoresGenericBadgeDefinition() {
    PubKeyTag pubKeyTag = new PubKeyTag(repDefnCreator.getPublicKey());
    BadgeDefinitionGenericEvent genericDefinition = new BadgeDefinitionGenericEvent(
       repDefnCreator, reputationIdentifierTag, relay);
    doReturn(List.of(
       genericDefinition.getGenericEventRecord(),
       event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(
          event.getKind(), pubKeyTag, reputationIdentifierTag);
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual =
       service.getBy(pubKeyTag, reputationIdentifierTag);

    assertEquals(eventId, actual.orElseThrow().getId());
  }

  @Test
  void testGetByKindPubKeyAndIdentifierTagReturnsEmptyOptional() {
    PubKeyTag pubKeyTag = new PubKeyTag(repDefnCreator.getPublicKey());
    doReturn(List.of())
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(
          event.getKind(), pubKeyTag, reputationIdentifierTag);
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual =
       service.getBy(pubKeyTag, reputationIdentifierTag);

    assertEquals(Optional.empty(), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       event.getKind(), pubKeyTag, reputationIdentifierTag);
  }

  @Test
  void testGetByDirectReturnsReputationDefinition() {
    AddressTag formulaAddressTag = curatedFormulaEvent.asAddressableEventAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagService)
       .getByDirect(Kind.BADGE_DEFINITION_EVENT, formulaAddressTag);
    mockFormulaEvent();
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual = service.getByDirect(formulaAddressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagService, Mockito.times(1))
       .getByDirect(Kind.BADGE_DEFINITION_EVENT, formulaAddressTag);
  }

  @Test
  void testGetByDirectIgnoresGenericBadgeDefinition() {
    AddressTag formulaAddressTag = curatedFormulaEvent.asAddressableEventAddressTag();
    BadgeDefinitionGenericEvent genericDefinition = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);
    doReturn(List.of(genericDefinition.getGenericEventRecord()))
       .when(cacheKindAddressTagService)
       .getByDirect(Kind.BADGE_DEFINITION_EVENT, formulaAddressTag);
    CacheBadgeDefinitionReputationEventService service = createService();

    Optional<BadgeDefinitionReputationEvent> actual = service.getByDirect(formulaAddressTag);

    assertEquals(Optional.empty(), actual);
  }

  @SneakyThrows
  @Override
  protected BadgeDefinitionReputationEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionGenericEvent =
       new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);

    this.curatedFormulaEvent = new CuratedFormulaEvent(aImgIdentity,
       new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, badgeDefinitionGenericEvent, PLUS_ONE_FORMULA, relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    return new BadgeDefinitionReputationEvent(
       aImgIdentity,
       repDefnCreator.getPublicKey(),
       reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       curatedFormulaEvent);
  }

  private CacheBadgeDefinitionReputationEventService createService() {
    return new CacheBadgeDefinitionReputationEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheReferenceAddressTagServiceIF,
       cacheCuratedFormulaEventServiceIF,
       cacheKindAddressTagService);
  }
}
