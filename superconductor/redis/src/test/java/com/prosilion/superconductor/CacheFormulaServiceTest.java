package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.tag.CacheReferenceEventTagService;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    verify(cacheReferenceEventTagService).getEvent(eventId, relay);
    verify(cacheReferenceAddressTagServiceIF).getByExpanded(event.requireFirstTag(AddressTag.class));
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
    verify(cacheReferenceAddressTagServiceIF).getByExpanded(
       new AddressTag(
          event.getKind(),
          formulaCreator.getPublicKey(),
          formulaUpvoteIdentifierTag,
          relay));
    verify(cacheReferenceEventTagService).getEvent(eventId, relay);
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
    verify(cacheKindAddressTagServiceIF).getByDirect(event.getKind(), addressTag);
    verify(cacheReferenceEventTagService).getEvent(eventId, relay);
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
