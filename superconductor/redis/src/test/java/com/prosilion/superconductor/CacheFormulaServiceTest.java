package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceAddressTagService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheFormulaServiceTest extends CacheServiceTestFixture<FormulaEvent> {
  public static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  public static final IdentifierTag formulaUpvoteIdentifierTag = new IdentifierTag(FORMULA_UNIT_UPVOTE);
  public static final String PLUS_ONE_FORMULA = "+1";

  final BadgeDefinitionGenericEvent awardDefinitionUpvoteEvent = new BadgeDefinitionGenericEvent(aImgIdentity, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);

  @Mock
  CacheReferenceEventTagService cacheReferenceEventTagService;
  @Mock
  CacheReferenceAddressTagServiceIF cacheReferenceAddressTagServiceIF;
  @Mock
  CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;

  @Test
  void testGetEventByEventId() {
    cacheReferenceEventTagServiceMock();
    cacheReferenceAddressTagServiceMock();
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    Optional<FormulaEvent> actual = cacheFormulaEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceEventTagService, Mockito.times(1)).getEvent(eventId, relay);
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
       formulaCreator.getPublicKey(), formulaUpvoteIdentifierTag, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(
       new AddressTag(
          event.getKind(),
          formulaCreator.getPublicKey(),
          formulaUpvoteIdentifierTag,
          relay));
    verify(cacheReferenceEventTagService, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetByKindPubKeyAndIdentifierTagReturnsEmptyOptional() {
    AddressTag addressTag = new AddressTag(
       event.getKind(),
       formulaCreator.getPublicKey(),
       formulaUpvoteIdentifierTag,
       relay);
    doReturn(Optional.empty())
       .when(cacheReferenceAddressTagServiceIF)
       .getByExpanded(addressTag);
    CacheFormulaEventService cacheFormulaEventService = createCacheFormulaEventService();

    Optional<FormulaEvent> actual = cacheFormulaEventService.getBy(
       formulaCreator.getPublicKey(), formulaUpvoteIdentifierTag, relay);

    assertEquals(Optional.empty(), actual);
    verify(cacheReferenceAddressTagServiceIF, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testGetByNonExistentAddressTagReturnsRemoteObject() {
    AddressTag formulaAddressTag = new AddressTag(
       event.getKind(),
       formulaCreator.getPublicKey(),
       formulaUpvoteIdentifierTag,
       relay);
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
       formulaCreator.getPublicKey(), formulaUpvoteIdentifierTag, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
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

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheKindAddressTagServiceIF, Mockito.times(1)).getByDirect(event.getKind(), addressTag);
    verify(cacheReferenceEventTagService, Mockito.times(1)).getEvent(eventId, relay);
  }

  @SneakyThrows
  @Override
  FormulaEvent createEvent() {
    return new FormulaEvent(formulaCreator, formulaUpvoteIdentifierTag, relay, awardDefinitionUpvoteEvent, PLUS_ONE_FORMULA);
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
