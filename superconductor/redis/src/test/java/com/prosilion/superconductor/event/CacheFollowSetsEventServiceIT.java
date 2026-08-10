package com.prosilion.superconductor.event;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.base.service.event.CacheFollowSetsEventService;
import com.prosilion.superconductor.base.BaseFollowSetsEventServiceIT;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class CacheFollowSetsEventServiceIT extends BaseFollowSetsEventServiceIT {
  private final CacheFollowSetsEventService cacheFollowSetsEventService;
  private final EventServiceIF eventServiceIF;

  @Autowired
  public CacheFollowSetsEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventService cacheFollowSetsEventService) {
    super(relayUrl, superconductorInstanceIdentity, cacheServiceIF, eventServiceIF, cacheFollowSetsEventService);
    this.cacheFollowSetsEventService = cacheFollowSetsEventService;
    this.eventServiceIF = eventServiceIF;
  }

  @Test
  void testConstructorRejectsNullDependencies() {
    CacheServiceIF cacheServiceIF = mock(CacheServiceIF.class);
    CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF =
       mock(CacheReferenceEventTagServiceIF.class);
    CacheBadgeAwardReputationEventServiceIF cacheBadgeAwardReputationEventServiceIF =
       mock(CacheBadgeAwardReputationEventServiceIF.class);
    CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF =
       mock(CacheKindAddressTagServiceIF.class);
    CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF =
       mock(CacheBadgeSetsEventServiceIF.class);

    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       null,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       null,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       null,
       cacheKindAddressTagServiceIF,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       null,
       cacheBadgeSetsEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheFollowSetsEventService(
       cacheServiceIF,
       cacheReferenceEventTagServiceIF,
       cacheBadgeAwardReputationEventServiceIF,
       cacheKindAddressTagServiceIF,
       null));
  }

  @Test
  void testMaterializeRejectsNullEvent() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.getEvent(Util.generateRandomHex64String(), null));
  }

  @Test
  void testGetBadgeAwardReputationEventsRejectsNullFollowSetsEvent() {
    assertThrows(NullPointerException.class, () ->
       cacheFollowSetsEventService.getBadgeAwardReputationEvents((FollowSetsEvent) null));
  }

  @Test
  void testGetByPubKeyTagRejectsNullPubKeyTag() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.getBy((PubKeyTag) null));
  }

  @Test
  void testGetByDirectRejectsNullEventTag() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventService.getByDirect(null));
  }

  @Test
  void testGetByPubKeyTag() {
    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       parameterAimgIdentity,
       badgeSetsUpvoteEvent,
       relay);
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent), followSetsEvent.getRelay().orElseThrow());
    
    List<FollowSetsEvent> actual = cacheFollowSetsEventService.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertFalse(actual.isEmpty());
  }

  @Test
  void testGetByDirectEventTag() {
    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       parameterAimgIdentity,
       badgeSetsUpvoteEvent,
       relay);
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent), followSetsEvent.getRelay().orElseThrow());
    
    EventTag eventTag = new EventTag(getBadgeSetsUpvoteEvent().getId(), relay.getUrl());
    Optional<FollowSetsEvent> actual = cacheFollowSetsEventService.getByDirect(eventTag);
    assertFalse(actual.isEmpty());
  }
}
