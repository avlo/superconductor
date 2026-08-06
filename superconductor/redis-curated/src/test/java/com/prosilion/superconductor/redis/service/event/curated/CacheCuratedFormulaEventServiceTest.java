package com.prosilion.superconductor.redis.service.event.curated;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedFormulaEventService;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.aImgIdentity;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.formulaCreator;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.relay;
import static com.prosilion.superconductor.base.BaseIntegrationTestFixtures.upvoteIdentifierTag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheCuratedFormulaEventServiceTest extends CacheCuratedServiceTestFixture<CuratedFormulaEvent> {
  private static final String FORMULA_UNIT_UPVOTE = "FORMULA_UNIT_UPVOTE";
  private static final IdentifierTag IDENTIFIER_TAG_FORMULA_UNIT_UPVOTE = new IdentifierTag(FORMULA_UNIT_UPVOTE);
  private static final String PLUS_ONE_FORMULA = "+1";

  @Mock
  CacheFormulaEventService cacheFormulaEventService;

  FormulaEvent formulaEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getEvent(curatedEventId, relay);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(curatedEventId);
    verify(cacheFormulaEventService, Mockito.times(0)).getEvent(curatedEventId, relay);
  }

  @Test
  void testGetByDirectAddressTagFromLocalCache() {
    AddressTag addressTag = curatedEvent.getAddressTag();
    doReturn(Optional.of(curatedEvent.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getByDirect(addressTag);

    assertEquals(curatedEventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1))
       .getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    verify(cacheFormulaEventService, Mockito.times(0)).getByDirect(addressTag);
  }

  @Test
  void testGetByDirectEventTagFromFormulaServiceAfterLocalMiss() {
    EventTag eventTag = curatedEvent.getEventTag();
    doReturn(Optional.empty()).when(cacheServiceIF)
       .getFirstEventByKindAndEventTag(Kind.CURATION_SETS_FORMULA_EVENT, eventTag);
    doReturn(Optional.of(formulaEvent)).when(cacheFormulaEventService)
       .getEvent(eventTag.getEventId(), eventTag.requireRelay());
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getByDirect(eventTag);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1))
       .getFirstEventByKindAndEventTag(Kind.CURATION_SETS_FORMULA_EVENT, eventTag);
    verify(cacheFormulaEventService, Mockito.times(1))
       .getEvent(eventTag.getEventId(), eventTag.requireRelay());
  }

  @Test
  void testGetByDirectAddressTagFromFormulaServiceAfterLocalMiss() {
    AddressTag addressTag = curatedEvent.getAddressTag();
    doReturn(Optional.empty()).when(cacheServiceIF)
       .getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    doReturn(Optional.of(formulaEvent)).when(cacheFormulaEventService).getByDirect(addressTag);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getByDirect(addressTag);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1))
       .getFirstEventByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    verify(cacheFormulaEventService, Mockito.times(1)).getByDirect(addressTag);
  }

  @SneakyThrows
  @Override
  protected CuratedFormulaEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionEvent =
       new BadgeDefinitionGenericEvent(aImgIdentity, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);
    this.formulaEvent = new FormulaEvent(
       formulaCreator,
       IDENTIFIER_TAG_FORMULA_UNIT_UPVOTE,
       badgeDefinitionEvent,
       PLUS_ONE_FORMULA,
       relay);
    return new CuratedFormulaEvent(
       aImgIdentity,
       formulaEvent,
       new ReferenceTag(formulaEvent.getRelay().map(Relay::getUrl).orElseThrow()),
       relay);
  }

  private CacheCuratedFormulaEventService createService() {
    return new CacheCuratedFormulaEventService(
       aImgIdentity,
       relay.getUrl(),
       cacheServiceIF,
       cacheFormulaEventService);
  }
}
