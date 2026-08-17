package java.com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.BadgeSetsEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheBadgeSetsEventService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.service.event.plugin.kind.type.SuperconductorKindType.BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheBadgeSetsEventServiceTest extends CacheServiceTestFixture<BadgeSetsEvent> {
  @Mock
  CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  @Mock
  CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF;
  @Mock
  CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF;

  BadgeDefinitionReputationEvent badgeDefinitionReputationEvent;
  CuratedBadgeAwardGenericEvent curatedBadgeAwardEvent;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       null,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheCuratedBadgeAwardGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       cacheServiceIF,
       null,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheCuratedBadgeAwardGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       null,
       cacheCuratedBadgeAwardGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createService().materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getEvent(eventId, null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createService().getByDirect((AddressTag) null));
  }

  @Test
  void testGetByPubKeyTagAndAddressTagRejectsNullParameters() {
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();
    AddressTag addressTag = badgeDefinitionReputationEvent.asAddressableEventAddressTag();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());

    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getBy(null, addressTag));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getBy(pubKeyTag, (AddressTag) null));
  }

  @Test
  void testGetByPubKeyTagRejectsNullPubKeyTag() {
    assertThrows(NullPointerException.class, () -> createService().getBy((PubKeyTag) null));
  }

  @Test
  void testGetByPubKeyTagAndEventTagRejectsNullParameters() {
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    EventTag eventTag = event.getTypeSpecificTags(EventTag.class).getFirst();

    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getBy(null, eventTag));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getBy(pubKeyTag, (EventTag) null));
  }

  @Test
  void testGetByPubKeyTagAndIdentifierTagRejectsNullParameters() {
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    IdentifierTag identifierTag = new IdentifierTag("identifier");

    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getBy(null, identifierTag));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventService.getBy(pubKeyTag, (IdentifierTag) null));
  }

  @Test
  void testGetEventByEventId() {
    mockMaterializationDependencies();
    mockLocalGetEventByEventId();
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    Optional<BadgeSetsEvent> actual = cacheBadgeSetsEventService.getEvent(eventId, relay);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
  }

  @Test
  void testGetByDirectAddressTag() {
    mockMaterializationDependencies();
    AddressTag addressTag = badgeDefinitionReputationEvent.asAddressableEventAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(event.getKind(), addressTag);
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    Optional<BadgeSetsEvent> actual = cacheBadgeSetsEventService.getByDirect(addressTag);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagServiceIF, Mockito.times(1)).getByDirect(event.getKind(), addressTag);
  }

  @Test
  void testGetByPubKeyTagAndAddressTag() {
    mockMaterializationDependencies();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    AddressTag addressTag = badgeDefinitionReputationEvent.asAddressableEventAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(event.getKind(), pubKeyTag, addressTag);
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    Optional<BadgeSetsEvent> actual = cacheBadgeSetsEventService.getBy(pubKeyTag, addressTag);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagServiceIF, Mockito.times(1)).getByDirect(event.getKind(), pubKeyTag, addressTag);
  }

  @Test
  void testGetByPubKeyTag() {
    mockMaterializationDependencies();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTag(event.getKind(), pubKeyTag);
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    List<BadgeSetsEvent> actual = cacheBadgeSetsEventService.getBy(pubKeyTag);

    assertEquals(List.of(eventId), actual.stream().map(BadgeSetsEvent::getId).toList());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTag(event.getKind(), pubKeyTag);
  }

  @Test
  void testGetByPubKeyTagAndEventTag() {
    mockMaterializationDependencies();
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    EventTag eventTag = event.getTypeSpecificTags(EventTag.class).getFirst();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndEventTag(event.getKind(), pubKeyTag, eventTag);
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    Optional<BadgeSetsEvent> actual = cacheBadgeSetsEventService.getBy(pubKeyTag, eventTag);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndEventTag(
       event.getKind(), pubKeyTag, eventTag);
  }

  @Test
  void testGetByPubKeyTagAndIdentifierTagReturnsEmptyOptional() {
    PubKeyTag pubKeyTag = new PubKeyTag(event.getPublicKey());
    IdentifierTag identifierTag = new IdentifierTag("missing");
    doReturn(List.of())
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(event.getKind(), pubKeyTag, identifierTag);
    CacheBadgeSetsEventService cacheBadgeSetsEventService = createService();

    Optional<BadgeSetsEvent> actual = cacheBadgeSetsEventService.getBy(pubKeyTag, identifierTag);

    assertEquals(Optional.empty(), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       event.getKind(), pubKeyTag, identifierTag);
  }

  @SneakyThrows
  @Override
  protected BadgeSetsEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionEvent = new BadgeDefinitionGenericEvent(
       BaseIntegrationTestFixtures.upvoteDefnCreator, BaseIntegrationTestFixtures.upvoteIdentifierTag, relay);

    CuratedFormulaEvent formulaEvent = new CuratedFormulaEvent(BaseIntegrationTestFixtures.aImgIdentity,
       new FormulaEvent(BaseIntegrationTestFixtures.formulaCreator, BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, badgeDefinitionEvent, "+1", relay),
       new ReferenceTag(relay.getUrl()),
       relay);

    this.badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       BaseIntegrationTestFixtures.aImgIdentity,
       BaseIntegrationTestFixtures.repDefnCreator.getPublicKey(),
       BaseIntegrationTestFixtures.reputationIdentifierTag,
       BADGE_DEFINITION_REPUTATION_EXTERNAL_IDENTITY_TAG,
       relay,
       formulaEvent);
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> badgeAwardEvent = new BadgeAwardGenericEvent<>(
       BaseIntegrationTestFixtures.submitter, BaseIntegrationTestFixtures.recipient.getPublicKey(), badgeDefinitionEvent, relay);
    this.curatedBadgeAwardEvent = new CuratedBadgeAwardGenericEvent(
       BaseIntegrationTestFixtures.aImgIdentity,
       badgeAwardEvent,
       new ReferenceTag(relay.getUrl()),
       new ReferenceTag(relay.getUrl()),
       relay);
    return new BadgeSetsEvent(
       BaseIntegrationTestFixtures.aImgIdentity, badgeDefinitionReputationEvent, curatedBadgeAwardEvent, relay);
  }

  private CacheBadgeSetsEventService createService() {
    return new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheCuratedBadgeAwardGenericEventServiceIF);
  }

  private void mockMaterializationDependencies() {
    doReturn(Optional.of(badgeDefinitionReputationEvent))
       .when(cacheBadgeDefinitionReputationEventServiceIF)
       .getByExpanded(badgeDefinitionReputationEvent.asAddressableEventAddressTag());
    doReturn(Optional.of(curatedBadgeAwardEvent))
       .when(cacheCuratedBadgeAwardGenericEventServiceIF)
       .getEvent(curatedBadgeAwardEvent.getId(), relay);
  }
}
