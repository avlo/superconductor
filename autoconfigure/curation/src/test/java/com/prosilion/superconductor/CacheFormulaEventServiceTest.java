package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.CacheServiceTestFixture;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.BaseIntegrationTestFixtures;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheFormulaEventServiceTest extends CacheServiceTestFixture<FormulaEvent> {
  final BadgeDefinitionGenericEvent awardDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(BaseIntegrationTestFixtures.aImgIdentity, BaseIntegrationTestFixtures.upvoteIdentifierTag, BaseIntegrationTestFixtures.PLUS_ONE_FORMULA, BaseIntegrationTestFixtures.relay);

  @Mock
  CacheReferenceEventTagService cacheReferenceEventTagService;
  @Mock
  CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  @Mock
  CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  @Test
  void testConstructorRejectsNullDependencies() {
    assertThrows(NullPointerException.class, () -> new CacheFormulaEventService(
       null,
       cacheReferenceAddressTagServiceIF,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFormulaEventService(
       cacheReferenceEventTagService,
       null,
       cacheKindAddressTagServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFormulaEventService(
       cacheReferenceEventTagService,
       cacheReferenceAddressTagServiceIF,
       null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getEvent(null, BaseIntegrationTestFixtures.relay));
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getEvent(eventId, null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> createCacheFormulaEventService().materialize((EventIF) null));
  }

  @Test
  void testGetByPublicKeyIdentifierTagAndRelayRejectsNullParameters() {
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();
    PublicKey publicKey = BaseIntegrationTestFixtures.formulaCreator.getPublicKey();

    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getBy(
       null, BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, BaseIntegrationTestFixtures.relay));
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getBy(
       publicKey, (IdentifierTag) null, BaseIntegrationTestFixtures.relay));
    assertThrows(NullPointerException.class, () -> cacheFormulaEventService.getBy(
       publicKey, BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> createCacheFormulaEventService().getByDirect(null));
  }

  @Test
  void testGetEventByEventId() {
    cacheReferenceEventTagServiceMock();
    cacheReferenceAddressTagServiceMock();
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    Optional<FormulaEvent> actual = cacheFormulaEventService.getEvent(eventId, BaseIntegrationTestFixtures.relay);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagService, Mockito.times(1)).getEvent(eventId, BaseIntegrationTestFixtures.relay);
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(
       event.requireFirstTag(AddressTag.class));
  }

  @Test
  void testGetByPublicKeyAndIdentifierTag() {
    cacheReferenceEventTagServiceMock();
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceAddressTagServiceIF)
       .getByExpanded(any(AddressTag.class));
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    Optional<FormulaEvent> actual = cacheFormulaEventService.getBy(
       BaseIntegrationTestFixtures.formulaCreator.getPublicKey(), BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, BaseIntegrationTestFixtures.relay);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(
       new AddressTag(
          event.getKind(),
          BaseIntegrationTestFixtures.formulaCreator.getPublicKey(),
          BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag,
          BaseIntegrationTestFixtures.relay));
    verify(cacheReferenceEventTagService, Mockito.times(1)).getEvent(eventId, BaseIntegrationTestFixtures.relay);
  }

  @Test
  void testGetByKindPubKeyAndIdentifierTagReturnsEmptyOptional() {
    AddressTag addressTag = new AddressTag(
       event.getKind(),
       BaseIntegrationTestFixtures.formulaCreator.getPublicKey(),
       BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag,
       BaseIntegrationTestFixtures.relay);
    doReturn(Optional.empty())
       .when(cacheReferenceAddressTagServiceIF)
       .getByExpanded(addressTag);
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    Optional<FormulaEvent> actual = cacheFormulaEventService.getBy(
       BaseIntegrationTestFixtures.formulaCreator.getPublicKey(), BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, BaseIntegrationTestFixtures.relay);

    assertEquals(Optional.empty(), actual);
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testGetByNonExistentAddressTagReturnsRemoteObject() {
    AddressTag formulaAddressTag = new AddressTag(
       event.getKind(),
       BaseIntegrationTestFixtures.formulaCreator.getPublicKey(),
       BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag,
       BaseIntegrationTestFixtures.relay);
    doReturn(Optional.empty())
       .when(cacheServiceIF)
       .getEventByKindAndAuthorPublicKeyAndIdentifierTag(any(), any(), any());
    doReturn(List.of(event.getGenericEventRecord()), List.of(awardDefinitionUpvoteEvent.getGenericEventRecord()))
       .when(remoteAbstractTagService)
       .sendRemoteReq(anyString(), any(Filters.class));
    cacheReferenceEventTagServiceMock();
    CacheFormulaEventService cacheFormulaEventService = new CacheFormulaEventService(
       cacheReferenceEventTagService,
       new CacheReferenceAddressTagService(cacheServiceIF, remoteAbstractTagService),
       cacheKindAddressTagServiceIF);

    Optional<FormulaEvent> actual = cacheFormulaEventService.getBy(
       BaseIntegrationTestFixtures.formulaCreator.getPublicKey(), BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, BaseIntegrationTestFixtures.relay);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       formulaAddressTag.getKind(),
       formulaAddressTag.publicKey(),
       formulaAddressTag.requireIdentifierTag());
    verify(remoteAbstractTagService, Mockito.times(2)).sendRemoteReq(
       anyString(), any(Filters.class));
  }

  @Test
  void testGetByDirectAddressTag() {
    cacheReferenceEventTagServiceMock();
    cacheReferenceAddressTagServiceMock();
    AddressTag addressTag = awardDefinitionUpvoteEvent.asAddressableEventAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheKindAddressTagServiceIF)
       .getByDirect(event.getKind(), addressTag);
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    Optional<FormulaEvent> actual = cacheFormulaEventService.getByDirect(addressTag);

    Assertions.assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagServiceIF, Mockito.times(1)).getByDirect(event.getKind(), addressTag);
    verify(cacheReferenceEventTagService, Mockito.times(1)).getEvent(eventId, BaseIntegrationTestFixtures.relay);
  }

  @SneakyThrows
  @Override
  protected FormulaEvent createEvent() {
    return new FormulaEvent(BaseIntegrationTestFixtures.formulaCreator, BaseIntegrationTestFixtures.formulaUpvoteIdentifierTag, awardDefinitionUpvoteEvent, BaseIntegrationTestFixtures.PLUS_ONE_FORMULA, BaseIntegrationTestFixtures.relay);
  }

  private CacheFormulaEventService createCacheFormulaEventService() {
    return new CacheFormulaEventService(
       cacheReferenceEventTagService,
       cacheReferenceAddressTagServiceIF,
       cacheKindAddressTagServiceIF);
  }

  private void cacheReferenceEventTagServiceMock() {
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheReferenceEventTagService)
       .getEvent(eq(eventId), any(Relay.class));
  }

  private void cacheReferenceAddressTagServiceMock() {
    doReturn(Optional.of(awardDefinitionUpvoteEvent.getGenericEventRecord()))
       .when(cacheReferenceAddressTagServiceIF)
       .getByExpanded(event.requireFirstTag(AddressTag.class));
  }
}
