package com.prosilion.superconductor;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.curation.plugin.kind.BadgeSetsEventKindPlugin;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheFollowSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheFollowSetsEventService;
import com.prosilion.superconductor.base.service.event.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.service.event.DeleteEventServiceIF;
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
public class CacheFollowSetsEventServiceIT extends BaseCacheFollowSetsEventServiceIT {
  private final CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF;
  private final EventServiceIF eventServiceIF;

  @Autowired
  public CacheFollowSetsEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull BadgeSetsEventKindPlugin badgeSetsEventKindPlugin,
     @NonNull DeleteEventServiceIF deleteEventServiceIF,
     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
     @NonNull @Qualifier("cacheFollowSetsEventService") CacheFollowSetsEventServiceIF cacheFollowSetsEventServiceIF) {
    super(relayUrl, superconductorInstanceIdentity, cacheServiceIF, badgeSetsEventKindPlugin, deleteEventServiceIF, eventServiceIF, cacheFollowSetsEventServiceIF);
    this.cacheFollowSetsEventServiceIF = cacheFollowSetsEventServiceIF;
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
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventServiceIF.materialize((EventIF) null));
  }

  @Test
  void testGetEventRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventServiceIF.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventServiceIF.getEvent(Util.generateRandomHex64String(), null));
  }

  @Test
  void testGetBadgeAwardReputationEventsRejectsNullFollowSetsEvent() {
    assertThrows(NullPointerException.class, () ->
       cacheFollowSetsEventServiceIF.getBadgeAwardReputationEvents((FollowSetsEvent) null));
  }

  @Test
  void testGetByPubKeyTagRejectsNullPubKeyTag() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventServiceIF.getBy((PubKeyTag) null));
  }

  @Test
  void testGetByDirectRejectsNullEventTag() {
    assertThrows(NullPointerException.class, () -> cacheFollowSetsEventServiceIF.getByDirect(null));
  }

  @Test
  void testGetByPubKeyTag() {
    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       superconductorInstanceIdentity,
       dbSynchedBadgeSetsEvent_1,
       relay);
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent), followSetsEvent.getRelay().orElseThrow());
    
    List<FollowSetsEvent> actual = cacheFollowSetsEventServiceIF.getBy(new PubKeyTag(recipient.getPublicKey()));
    assertFalse(actual.isEmpty());
  }

  @Test
  void testGetByAddressTag() {
    FollowSetsEvent followSetsEvent = new FollowSetsEvent(
       superconductorInstanceIdentity,
       dbSynchedBadgeSetsEvent_1,
       relay);
    eventServiceIF.processIncomingEvent(new EventMessage(followSetsEvent), followSetsEvent.getRelay().orElseThrow());
    
    Optional<FollowSetsEvent> actual = cacheFollowSetsEventServiceIF.getByDirect(getBadgeSetsUpvoteEvent().asAddressableEventAddressTag());
    assertFalse(actual.isEmpty());

    assertThrows(NostrException.class, () -> new FollowSetsEvent(followSetsEvent.asGenericEventRecord(), List.of()));
  }
}
