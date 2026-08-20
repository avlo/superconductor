package com.prosilion.superconductor.supplier.local;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.supplier.local.abstracts.AbstractBadgeAwardReputationEventMessageSupplierLocalListIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.util.List;
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
  void aSuperconductorEventThenAfterimageReq() throws NostrException {
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> upvoteEvent_1 = createUpvoteEvent(new Relay(definitionEventRelayUrl), recipient.getPublicKey());

    submitSCEventWithDuration_backup(
       upvoteEvent_1,
       definitionEventRelayUrl,
       new Filters(
          new ReferencedPublicKeyFilter(
             new PubKeyTag(recipient.getPublicKey())),
          new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT)));

    assertEquals(
       "1",
       submitAfterImageReq(new PubKeyTag(recipient.getPublicKey()), awardEventRelayUrl).getFirst().getContent());

//    below is different recipient, works fine
//    PublicKey newRecipient = Identity.generateRandomIdentity().getPublicKey();
//    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> upvoteEvent2 = createUpvoteEvent(new Relay(definitionEventRelayUrl), newRecipient);
//
//    submitSCEventWithDuration_backup(
//       upvoteEvent2,
//       definitionEventRelayUrl,
//       new Filters(
//          new ReferencedPublicKeyFilter(
//             new PubKeyTag(newRecipient)),
//          new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT)));
//
//    assertEquals(
//       "1",
//       submitAfterImageReq(new PubKeyTag(newRecipient), awardEventRelayUrl).getFirst().getContent());

//    below is same 1st recipient, fails    
    BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> upvoteEvent_3 = createUpvoteEvent(new Relay(definitionEventRelayUrl), recipient.getPublicKey());

    List<EventIF> eventIFS = submitSCEventWithDuration_backup(
       upvoteEvent_3,
       definitionEventRelayUrl,
       new Filters(
          new ReferencedPublicKeyFilter(
             new PubKeyTag(recipient.getPublicKey())),
          new KindFilter(Kind.CURATION_SETS_BADGE_AWARD_EVENT)));

    assertEquals(
       "2",
       submitAfterImageReq(new PubKeyTag(recipient.getPublicKey()), awardEventRelayUrl).getFirst().getContent());
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createUpvoteEvent(Relay relay, PublicKey recipientPublicKey) {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipientPublicKey,
       badgeDefinitionGenericEventList.getFirst(),
       relay);
  }

  protected BadgeAwardGenericEvent<BadgeDefinitionGenericEvent> createDownvoteEvent(Relay relay) {
    return new BadgeAwardGenericEvent<>(
       submitter,
       recipient.getPublicKey(),
       badgeDefinitionGenericEventList.get(1),
       relay);
  }
}
