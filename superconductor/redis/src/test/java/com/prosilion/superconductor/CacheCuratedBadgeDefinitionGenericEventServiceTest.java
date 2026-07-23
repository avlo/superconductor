package com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.AbstractSetsEvent;
import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionGenericEventService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CacheCuratedBadgeDefinitionGenericEventServiceTest extends CacheServiceTestFixture<CuratedBadgeDefinitionGenericEvent> {
  @Mock
  CacheBadgeDefinitionGenericEventService cacheBadgeDefinitionGenericEventService;

  BadgeDefinitionGenericEvent badgeDefinitionGenericEvent;

  @Test
  void testGetEventFromLocalCache() {
    mockLocalGetEventByEventId();
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getEvent(eventId, relay);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetEventFromBadgeDefinitionServiceAfterLocalMiss() {
    doReturn(Optional.empty()).when(cacheServiceIF).getEventByEventId(eventId);
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getEvent(eventId, event.getRelay().orElseThrow());
    
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getEvent(eventId, event.getRelay().orElseThrow());

    assertEquals(event.getEventTag(), actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventByEventId(eventId);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getEvent(eventId, relay);
  }

  @Test
  void testGetByDirectFromLocalCache() {
    EventTag eventTag = event.getEventTag();
    doReturn(List.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventsByKindAndEventTag(Kind.CURATION_SETS, eventTag);

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(eventTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    assertEquals(eventTag, actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndEventTag(Kind.CURATION_SETS, eventTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetByDirectFromBadgeDefinitionServiceAfterLocalMiss() {
    EventTag eventTag = event.getEventTag();
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndEventTag(Kind.CURATION_SETS, eventTag);
//    new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay)
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getEvent(eventTag.getEventId(), eventTag.requireRelay());

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect(eventTag);

    assertEquals(eventTag, actual.map(AbstractSetsEvent::getEventTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndEventTag(Kind.CURATION_SETS, eventTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getEvent(eventTag.getEventId(), eventTag.requireRelay());
  }

  @Test
  void testGetByAddressTagFromBadgeDefinitionServiceAfterLocalMiss() {
    AddressTag addressTag = event.getAddressTag();
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndAddressTag(Kind.CURATION_SETS, addressTag);
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getByExpanded(addressTag);
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getBy(addressTag);

    assertEquals(addressTag, actual.map(AbstractSetsEvent::getAddressTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndAddressTag(Kind.CURATION_SETS, addressTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testGetByAddressTagReturnsEmptyOptionalWhenNotFoundLocallyOrRemotely() {
    AddressTag addressTag = event.getAddressTag();
    doReturn(List.of()).when(cacheServiceIF).getEventsByKindAndAddressTag(Kind.CURATION_SETS, addressTag);
    doReturn(Optional.empty())
       .when(cacheBadgeDefinitionGenericEventService)
       .getByExpanded(addressTag);
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getBy(addressTag);

    assertEquals(Optional.empty(), actual);
    verify(cacheServiceIF, Mockito.times(1)).getEventsByKindAndAddressTag(Kind.CURATION_SETS, addressTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getByExpanded(addressTag);
  }

  @Test
  void testGetByPublicKeyAndIdentifierTagFromLocalCache() {
    IdentifierTag identifierTag = new IdentifierTag(String.valueOf(event.getAddressTag().hashCode()));
    
    doReturn(Optional.of(event.getGenericEventRecord()))
       .when(cacheServiceIF)
       .getEventByKindAndAuthorPublicKeyAndIdentifierTag(
          Kind.CURATION_SETS,
          event.getPublicKey(),
          identifierTag);

    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService = createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getBy(event.getPublicKey(), identifierTag);

    assertEquals(eventId, actual.orElseThrow().getId());
    verify(cacheServiceIF, Mockito.times(1)).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       Kind.CURATION_SETS, event.getPublicKey(), identifierTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(0)).getEvent(eventId, relay);
  }

  @Test
  void testGetByPublicKeyAndIdentifierTagFromBadgeDefinitionServiceAfterLocalMiss() {
    IdentifierTag identifierTag = new IdentifierTag(String.valueOf(event.getAddressTag().hashCode()));
    PubKeyTag authorPubkeyTag = new PubKeyTag(event.getPublicKey());

    doReturn(Optional.empty()).when(cacheServiceIF).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       Kind.CURATION_SETS, event.getPublicKey(), identifierTag);
    doReturn(Optional.of(badgeDefinitionGenericEvent))
       .when(cacheBadgeDefinitionGenericEventService)
       .getBy(authorPubkeyTag, identifierTag);
    
    CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService =
       createService();

    Optional<CuratedBadgeDefinitionGenericEvent> actual =
       cacheCuratedBadgeDefinitionGenericEventService.getBy(event.getPublicKey(), identifierTag);

    assertEquals(identifierTag, actual.map(AddressableEvent::getIdentifierTag).orElseThrow());
    verify(cacheServiceIF, Mockito.times(1)).getEventByKindAndAuthorPublicKeyAndIdentifierTag(
       Kind.CURATION_SETS, event.getPublicKey(), identifierTag);
    verify(cacheBadgeDefinitionGenericEventService, Mockito.times(1)).getBy(
       authorPubkeyTag, identifierTag);
  }
  
  @Override
  CuratedBadgeDefinitionGenericEvent createEvent() {
    this.badgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(upvoteDefnCreator, upvoteIdentifierTag, relay);
    return new CuratedBadgeDefinitionGenericEvent(
       aImgIdentity,
       this.badgeDefinitionGenericEvent,
       relay);
  }

  private CacheCuratedBadgeDefinitionGenericEventService createService() {
    return new CacheCuratedBadgeDefinitionGenericEventService(
       aImgIdentity,
       relay.getUrl(),
       cacheServiceIF,
       cacheBadgeDefinitionGenericEventService);
  }
}
