package com.prosilion.superconductor.base;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.curated.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseCacheCuratedBadgeDefinitionGenericEventServiceIT extends BaseIntegrationTestFixtures {
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;
  private final Relay relay;

  private final CuratedBadgeDefinitionGenericEvent curatedUpvoteDefinitionEvent;
  private final BadgeDefinitionGenericEvent downvoteDefinitionEvent;

  public BaseCacheCuratedBadgeDefinitionGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF) {
    super(superconductorInstanceIdentity);
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
    this.relay = new Relay(relayUrl);

    BadgeDefinitionGenericEvent upvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, upvoteIdentifierTag, relay);

    this.curatedUpvoteDefinitionEvent = new CuratedBadgeDefinitionGenericEvent(
       parameterAimgIdentity,
       upvoteDefinitionEvent,
       new ReferenceTag(relayUrl),
       relay);

    cacheServiceIF.save(curatedUpvoteDefinitionEvent);

    this.downvoteDefinitionEvent = new BadgeDefinitionGenericEvent(
       upvoteDefnCreator, downvoteIdentifierTag, relay);
    cacheServiceIF.save(downvoteDefinitionEvent);
  }

  @Test
  public void testGetEventByEventIdRelay() {
    Optional<CuratedBadgeDefinitionGenericEvent> byEventIdRelay = cacheCuratedBadgeDefinitionGenericEventServiceIF
       .getEvent(curatedUpvoteDefinitionEvent.getEventId(), curatedUpvoteDefinitionEvent.getRelay().orElseThrow());
    assertTrue(byEventIdRelay.isPresent());
  }

  @Test
  public void testGetEventByDirectEventTag() {
    Optional<CuratedBadgeDefinitionGenericEvent> byEventTag = cacheCuratedBadgeDefinitionGenericEventServiceIF
       .getByDirect(curatedUpvoteDefinitionEvent.getEventTag());
    assertTrue(byEventTag.isPresent());
    assertEquals(curatedUpvoteDefinitionEvent, byEventTag.orElseThrow());
  }

  @Test
  public void testGetEventByAddressTag() {
    Optional<CuratedBadgeDefinitionGenericEvent> byAddressTag = cacheCuratedBadgeDefinitionGenericEventServiceIF
       .getByDirect(curatedUpvoteDefinitionEvent.getAddressTag());
    assertTrue(byAddressTag.isPresent());
    assertEquals(curatedUpvoteDefinitionEvent, byAddressTag.orElseThrow());
  }

  @Test
  public void testGetByDirectEventTagFromBackingServiceAfterLocalMiss() {
    CuratedBadgeDefinitionGenericEvent expected = new CuratedBadgeDefinitionGenericEvent(
       parameterAimgIdentity,
       downvoteDefinitionEvent,
       new ReferenceTag(relay.getUrl()),
       relay);

    CuratedBadgeDefinitionGenericEvent actual =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(expected.getEventTag()).orElseThrow();

    assertEquals(expected.getEventTag(), actual.getEventTag());
  }

  @Test
  public void testGetByDirectAddressTagFromBackingServiceAfterLocalMiss() {
    CuratedBadgeDefinitionGenericEvent expected = new CuratedBadgeDefinitionGenericEvent(
       parameterAimgIdentity,
       this.downvoteDefinitionEvent,
       new ReferenceTag(relay.getUrl()),
       relay);

    CuratedBadgeDefinitionGenericEvent actual =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(expected.getAddressTag()).orElseThrow();

    assertEquals(expected.getAddressTag(), actual.getAddressTag());
  }

  @Test
  public void testNonExistentEventIdReturnsEmptyOptional() {
    String nonExistentEventId = Util.generateRandomHex64String();
    assertEquals(Optional.empty(), cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(nonExistentEventId, relay));
  }

  @Test
  public void testNonExistentEventTagEventIdThrowsException() {
    String nonExistentEventId = Util.generateRandomHex64String();
    EventTag nonExistentEventTagEventId = new EventTag(nonExistentEventId);
    assertThrows(NostrException.class, () -> cacheCuratedBadgeDefinitionGenericEventServiceIF.getByDirect(nonExistentEventTagEventId));
  }
}
