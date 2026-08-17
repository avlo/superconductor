package java.com.prosilion.superconductor;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheBadgeSetsEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeAwardGenericEventServiceIF;
import com.prosilion.superconductor.autoconfigure.curation.service.event.sets.CacheBadgeSetsEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class CacheBadgeSetsEventServiceIT extends BaseBadgeSetsEventServiceIT {
  private final CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF;

  @Autowired
  public CacheBadgeSetsEventServiceIT(
     @NonNull @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("cacheBadgeSetsEventService") CacheBadgeSetsEventServiceIF cacheBadgeSetsEventServiceIF) {
    super(relayUrl, superconductorInstanceIdentity, cacheServiceIF, cacheBadgeSetsEventServiceIF);
    this.cacheBadgeSetsEventServiceIF = cacheBadgeSetsEventServiceIF;
  }

  @Test
  void testConstructorRejectsNullDependencies() {
    CacheServiceIF cacheServiceIF = mock(CacheServiceIF.class);
    CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF = mock(CacheKindAddressTagServiceIF.class);
    CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventServiceIF =
       mock(CacheBadgeDefinitionReputationEventServiceIF.class);
    CacheCuratedBadgeAwardGenericEventServiceIF cacheCuratedBadgeAwardGenericEventServiceIF =
       mock(CacheCuratedBadgeAwardGenericEventServiceIF.class);

    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       null,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheCuratedBadgeAwardGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       cacheServiceIF,
       null,
       cacheBadgeDefinitionReputationEventServiceIF,
       cacheCuratedBadgeAwardGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       null,
       cacheCuratedBadgeAwardGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheBadgeSetsEventService(
       cacheServiceIF,
       cacheKindAddressTagServiceIF,
       cacheBadgeDefinitionReputationEventServiceIF,
       null));
  }

//  @Test
//  void testMaterializeRejectsNullEvent() {
//    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.materialize((EventIF) null));
//  }

  @Test
  void testGetEventRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getEvent(null, relay));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getEvent(Util.generateRandomHex64String(), null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getByDirect(null));
  }

  @Test
  void testGetByPubKeyTagAndAddressTagRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy(null, new AddressTag(Kind.TEXT_NOTE, submitter.getPublicKey(), upvoteIdentifierTag)));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy(new PubKeyTag(submitter.getPublicKey()), (AddressTag) null));
  }

  @Test
  void testGetByPubKeyTagRejectsNullPubKeyTag() {
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy((PubKeyTag) null));
  }

  @Test
  void testGetByPubKeyTagAndEventTagRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy(null, new EventTag(Util.generateRandomHex64String())));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy(new PubKeyTag(submitter.getPublicKey()), (EventTag) null));
  }

  @Test
  void testGetByPubKeyTagAndIdentifierTagRejectsNullParameters() {
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy(null, upvoteIdentifierTag));
    assertThrows(NullPointerException.class, () -> cacheBadgeSetsEventServiceIF.getBy(new PubKeyTag(submitter.getPublicKey()), (IdentifierTag) null));
  }
}
