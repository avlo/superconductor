package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedFormulaEvent;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFormulaEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedFormulaEventService;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheCuratedFormulaEventServiceTest extends CacheServiceTestFixture<CuratedFormulaEvent> {
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

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheFormulaEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetEventFromFormulaServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(Optional.of(formulaEvent)).when(cacheFormulaEventService).getEvent(eventId, relay);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getEvent(eventId, relay);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheFormulaEventService, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetByPublicKeyAndIdentifierTagFromLocalCache() {
    IdentifierTag identifierTag = IDENTIFIER_TAG_FORMULA_UNIT_UPVOTE;
    PubKeyTag publicKeyTag = new PubKeyTag(event.getPublicKey());
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndPubKeyTagAndIdentifierTag(
          Kind.CURATION_SETS_FORMULA_EVENT, publicKeyTag, identifierTag);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual =
       cacheCuratedFormulaEventService.getBy(event.getPublicKey(), identifierTag, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       Kind.CURATION_SETS_FORMULA_EVENT, publicKeyTag, identifierTag);
    verify(cacheFormulaEventService, Mockito.times(0))
       .getBy(event.getPublicKey(), identifierTag, relay);
  }

  @Test
  void testGetByPublicKeyAndIdentifierTagFromFormulaServiceAfterLocalMiss() {
    IdentifierTag identifierTag = IDENTIFIER_TAG_FORMULA_UNIT_UPVOTE;
    PubKeyTag publicKeyTag = new PubKeyTag(event.getPublicKey());
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndPubKeyTagAndIdentifierTag(
       Kind.CURATION_SETS_FORMULA_EVENT, publicKeyTag, identifierTag);
    doReturn(Optional.of(formulaEvent))
       .when(cacheFormulaEventService)
       .getBy(event.getPublicKey(), identifierTag, relay);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual =
       cacheCuratedFormulaEventService.getBy(event.getPublicKey(), identifierTag, relay);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndPubKeyTagAndIdentifierTag(
       Kind.CURATION_SETS_FORMULA_EVENT, publicKeyTag, identifierTag);
    verify(cacheFormulaEventService, Mockito.times(1))
       .getBy(event.getPublicKey(), identifierTag, relay);
  }

  @Test
  void testGetByDirectFromLocalCache() {
    AddressTag addressTag = event.getAddressTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getByDirect(addressTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1))
       .getEventsByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    verify(cacheFormulaEventService, Mockito.times(0)).getByDirect(addressTag);
  }

  @Test
  void testGetByDirectFromFormulaServiceAfterLocalMiss() {
    AddressTag addressTag = event.getAddressTag();
    doReturn(List.of()).when(cacheServiceIF)
       .getEventsByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    doReturn(Optional.of(formulaEvent)).when(cacheFormulaEventService).getByDirect(addressTag);
    CacheCuratedFormulaEventService cacheCuratedFormulaEventService = createService();

    Optional<CuratedFormulaEvent> actual = cacheCuratedFormulaEventService.getByDirect(addressTag);

    assertTrue(actual.isPresent());
    verify(cacheServiceIF, Mockito.times(1))
       .getEventsByKindAndAddressTag(Kind.CURATION_SETS_FORMULA_EVENT, addressTag);
    verify(cacheFormulaEventService, Mockito.times(1)).getByDirect(addressTag);
  }

  @SneakyThrows
  @Override
  CuratedFormulaEvent createEvent() {
    BadgeDefinitionGenericEvent badgeDefinitionEvent =
       new BadgeDefinitionGenericEvent(aImgIdentity, upvoteIdentifierTag, PLUS_ONE_FORMULA, relay);
    this.formulaEvent = new FormulaEvent(
       formulaCreator,
       IDENTIFIER_TAG_FORMULA_UNIT_UPVOTE,
       relay,
       badgeDefinitionEvent,
       PLUS_ONE_FORMULA);
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
