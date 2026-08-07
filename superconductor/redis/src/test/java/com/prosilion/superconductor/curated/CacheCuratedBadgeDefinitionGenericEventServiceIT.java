package com.prosilion.superconductor.curated;

import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedBadgeDefinitionGenericEventService;
import com.prosilion.superconductor.base.curated.BaseCacheCuratedBadgeDefinitionGenericEventServiceIT;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheCuratedBadgeDefinitionGenericEventServiceIT extends BaseCacheCuratedBadgeDefinitionGenericEventServiceIT {
  private final CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService;

  @Autowired
  public CacheCuratedBadgeDefinitionGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheCuratedBadgeDefinitionGenericEventService cacheCuratedBadgeDefinitionGenericEventService) {
    super(relayUrl, superconductorInstanceIdentity, cacheServiceIF, cacheCuratedBadgeDefinitionGenericEventService);
    this.cacheCuratedBadgeDefinitionGenericEventService = cacheCuratedBadgeDefinitionGenericEventService;
  }

  @Test
  void testConstructorRejectsNullDependencies() {
    Identity instanceIdentity = mock(Identity.class);
    CacheServiceIF cacheServiceIF = mock(CacheServiceIF.class);
    CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF =
       mock(CacheBadgeDefinitionGenericEventServiceIF.class);

    assertThrows(NullPointerException.class, () -> new CacheCuratedBadgeDefinitionGenericEventService(
       null,
       relay.getUrl(),
       cacheServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheCuratedBadgeDefinitionGenericEventService(
       instanceIdentity,
       null,
       cacheServiceIF,
       cacheBadgeDefinitionGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheCuratedBadgeDefinitionGenericEventService(
       instanceIdentity,
       relay.getUrl(),
       null,
       cacheBadgeDefinitionGenericEventServiceIF));
    assertThrows(NullPointerException.class, () -> new CacheCuratedBadgeDefinitionGenericEventService(
       instanceIdentity,
       relay.getUrl(),
       cacheServiceIF,
       null));
  }

  @Test
  void testGetByDirectRejectsNullEventTag() {
    assertThrows(NullPointerException.class, () ->
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect((EventTag) null));
  }

  @Test
  void testGetByDirectRejectsNullAddressTag() {
    assertThrows(NullPointerException.class, () ->
       cacheCuratedBadgeDefinitionGenericEventService.getByDirect((AddressTag) null));
  }
}
