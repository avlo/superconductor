//package com.prosilion.superconductor.redis.service.event;
//
//import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
//import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
//import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.nostr.message.EventMessage;
//import com.prosilion.nostr.tag.AddressTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.tag.RelayTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventAuxServiceIF;
//import com.prosilion.superconductor.base.cache.CacheServiceIF;
//import com.prosilion.superconductor.base.cache.mapped.CacheTagMappedEventServiceIF;
//import com.prosilion.superconductor.base.service.event.EventServiceIF;
//import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
//import java.util.List;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static com.prosilion.superconductor.redis.config.DataLoaderRedisTestIF.TEST_UNIT_UPVOTE;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@Slf4j
//@EmbeddedRedisStandalone
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//public class CacheBadgeAwardGenericEventAuxServiceIT {
//  public final IdentifierTag upvoteIdentifierTag = new IdentifierTag(TEST_UNIT_UPVOTE);
//  public final Identity identity = Identity.generateRandomIdentity();
//
//  private final BadgeDefinitionGenericEventAux awardUpvoteDefinitionEventAux;
//  private final CacheBadgeAwardGenericEventAuxServiceIF<BadgeDefinitionGenericEventAux, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>> cacheBadgeAwardGenericEventAuxServiceIF;
//  private final EventServiceIF eventServiceIF;
//  private final Relay relay;
//
//  @Autowired
//  public CacheBadgeAwardGenericEventAuxServiceIT(
//     @Value("${superconductor.relay.url}") String relayUri,
//     @NonNull CacheServiceIF cacheServiceIF,
//     @NonNull @Qualifier("eventService") EventServiceIF eventServiceIF,
//     @NonNull @Qualifier("cacheBadgeAwardGenericEventAuxService") CacheTagMappedEventServiceIF<BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>, AddressTag> cacheBadgeAwardGenericEventServiceIF) {
//    this.eventServiceIF = eventServiceIF;
//    this.cacheBadgeAwardGenericEventAuxServiceIF = (CacheBadgeAwardGenericEventAuxServiceIF<BadgeDefinitionGenericEventAux, BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux>>) cacheBadgeAwardGenericEventServiceIF;
//    this.relay = new Relay(relayUri);
//
//    awardUpvoteDefinitionEventAux = fakedBadgeDefinitionGenericEvent(
//       new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay), upvoteIdentifierTag);
//    
//    cacheServiceIF.save(awardUpvoteDefinitionEventAux);
//  }
//
//  @Test
//  public void testValidateSaveMaterializedBadgeAwardGenericEventUpvote() {
//    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
//
//    BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux> badgeAwardGenericVoteEvent = new BadgeAwardGenericEventAux<>(
//       identity,
//       upvotedUserPublicKey,
//       relay,
//       fakedBadgeDefinitionGenericEvent(
//          new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay), upvoteIdentifierTag));
//
//    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericVoteEvent), relay);
//    BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux> dbMaterializedGenericAwardEvent =
//       cacheBadgeAwardGenericEventAuxServiceIF.materialize(badgeAwardGenericVoteEvent).get();
//    
//    assertEquals(badgeAwardGenericVoteEvent, dbMaterializedGenericAwardEvent);
//  }
//
//  @Test
//  public void testValidateGetEventSaveBadgeAwardGenericEventUpvote() {
//    PublicKey upvotedUserPublicKey = Identity.generateRandomIdentity().getPublicKey();
//
//    BadgeDefinitionGenericEventAux badgeDefinitionGenericEventAux = fakedBadgeDefinitionGenericEvent(
//       new BadgeDefinitionGenericEvent(identity, upvoteIdentifierTag, relay), upvoteIdentifierTag);
//    
//    BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux> badgeAwardGenericVoteEvent = new BadgeAwardGenericEventAux<>(
//       identity,
//       upvotedUserPublicKey,
//       relay,
//       badgeDefinitionGenericEventAux);
//
//    eventServiceIF.processIncomingEvent(new EventMessage(badgeAwardGenericVoteEvent), relay);
//
//    BadgeAwardGenericEventAux<BadgeDefinitionGenericEventAux> dbGetEventGenericAwardEvent =
//       cacheBadgeAwardGenericEventAuxServiceIF.getEvent(
//          badgeAwardGenericVoteEvent.getId(),
//          badgeAwardGenericVoteEvent.getRelayTag().map(RelayTag::getRelay).map(Relay::getUrl).orElseThrow()).orElseThrow();
//    assertEquals(badgeAwardGenericVoteEvent, dbGetEventGenericAwardEvent);
//  }
//
//  public static BadgeDefinitionGenericEventAux fakedBadgeDefinitionGenericEvent(BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent, IdentifierTag identifierTag) {
//    GenericEventRecord genericEventRecord =
//       new GenericEventRecord(
//          badgeDefinitionUpvoteEvent.getId(),
//          badgeDefinitionUpvoteEvent.getPublicKey(),
//          badgeDefinitionUpvoteEvent.getCreatedAt(),
//          badgeDefinitionUpvoteEvent.getKind(),
//          List.of(identifierTag),
//          badgeDefinitionUpvoteEvent.getContent(),
//          badgeDefinitionUpvoteEvent.getSignature());
//
//    BadgeDefinitionGenericEvent fakedGenericEvent = new BadgeDefinitionGenericEvent(genericEventRecord);
//
//    return new BadgeDefinitionGenericEventAux(fakedGenericEvent, new RelayTag(new Relay("ws://localhost-placeholder-for-test")));
//  }
//}
