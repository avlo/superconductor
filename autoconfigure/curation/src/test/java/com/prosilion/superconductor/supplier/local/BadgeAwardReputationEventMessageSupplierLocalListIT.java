package com.prosilion.superconductor.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.supplier.local.abstracts.AbstractBadgeAwardReputationEventMessageSupplierLocalListIT;
import com.prosilion.superconductor.util.EventAttributesMap;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class BadgeAwardReputationEventMessageSupplierLocalListIT extends AbstractBadgeAwardReputationEventMessageSupplierLocalListIT {
  private final CacheServiceIF cacheServiceIF;

  @Autowired
  public BadgeAwardReputationEventMessageSupplierLocalListIT(
     CacheServiceIF cacheServiceIF,
     @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity) {
    super(superconductorInstanceIdentity, superconductorRelayUrl, superconductorRelayUrl);
    this.cacheServiceIF = cacheServiceIF;
  }

  @Test
  void bSuperconductorEventThenAfterimageReq() throws NostrException {
    createAndSubmitDownvoteEvent("-1", getIdenticalDownvoteRecipientEvent());
    createAndSubmitUpvoteEvent("0", getIdenticalUpvoteRecipientEvent());
  }

  @Test
  void cSuperconductorEventThenAfterimageReq() throws NostrException {
    createAndSubmitUpvoteEvent("1", getIdenticalUpvoteRecipientEvent());
    createAndSubmitUpvoteEvent("2", getIdenticalUpvoteRecipientEvent());
    createAndSubmitDownvoteEvent("1", getIdenticalUpvoteRecipientEvent());
  }
  
    @Test
  void aSuperconductorEventThenAfterimageReq() throws NostrException {
    createAndSubmitUpvoteEvent("1", getIdenticalUpvoteRecipientEvent());
    createAndSubmitUpvoteEvent("1", getIdenticalUpvoteDifferentRecipientEvent());
    createAndSubmitUpvoteEvent("2", getIdenticalUpvoteRecipientEvent());
    createAndSubmitUpvoteEvent("3", getIdenticalUpvoteRecipientEvent());
    createAndSubmitUpvoteEvent("2", getIdenticalUpvoteDifferentRecipientEvent());

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> identicalUpvoteRecipientEvent = getIdenticalUpvoteRecipientEvent();
    createAndSubmitUpvoteEvent("4", identicalUpvoteRecipientEvent);
    createAndSubmitUpvoteEvent("4", identicalUpvoteRecipientEvent);

    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> identicalUpvoteDifferentRecipientEvent = getIdenticalUpvoteDifferentRecipientEvent();
    createAndSubmitUpvoteEvent("3", identicalUpvoteDifferentRecipientEvent);
    createAndSubmitUpvoteEvent("3", identicalUpvoteDifferentRecipientEvent);

    createAndSubmitDownvoteEvent("5", getIdenticalUpvoteRecipientEvent());
    createAndSubmitDownvoteEvent("4", getIdenticalDownvoteRecipientEvent());
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> getIdenticalUpvoteRecipientEvent() {
    return createUpvoteEvent(new Relay(definitionEventRelayUrl), recipient.getPublicKey());
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> getIdenticalDownvoteRecipientEvent() {
    return createDownvoteEvent(new Relay(definitionEventRelayUrl), recipient.getPublicKey());
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> getIdenticalUpvoteDifferentRecipientEvent() {
    return createUpvoteEvent(new Relay(definitionEventRelayUrl), recipientDifferent.getPublicKey());
  }

  private BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> getIdenticalDownvoteDifferentRecipientEvent() {
    return createDownvoteEvent(new Relay(definitionEventRelayUrl), recipientDifferent.getPublicKey());
  }

  private void createAndSubmitUpvoteEvent(String expectedScore, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> event) {
    submitSCEventWithDuration_backup(
       event,
       definitionEventRelayUrl,
       new Filters(
          new ReferencedPublicKeyFilter(
             new PubKeyTag(event.getAwardRecipientPublicKey())),
          new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT)));

    assertEquals(
       expectedScore,
       submitAfterImageReq(new PubKeyTag(event.getAwardRecipientPublicKey()), awardEventRelayUrl).getFirst().getContent());
  }

  private void createAndSubmitDownvoteEvent(String expectedScore, BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> event) {
    submitSCEventWithDuration_backup(
       event,
       definitionEventRelayUrl,
       new Filters(
          new ReferencedPublicKeyFilter(
             new PubKeyTag(event.getAwardRecipientPublicKey())),
          new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT)));

    assertEquals(
       expectedScore,
       submitAfterImageReq(new PubKeyTag(event.getAwardRecipientPublicKey()), awardEventRelayUrl).getFirst().getContent());
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createUpvoteEvent(Relay relay, PublicKey recipientPublicKey) {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipientPublicKey,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, upvoteIdentifierTag),
       relay);
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createDownvoteEvent(Relay relay, PublicKey recipientPublicKey) {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipientPublicKey,
       EventAttributesMap.getFirstByIdentifierTag(
          this.badgeDefinitionGenericEventList, downvoteIdentifierTag),
       relay);
  }
}
