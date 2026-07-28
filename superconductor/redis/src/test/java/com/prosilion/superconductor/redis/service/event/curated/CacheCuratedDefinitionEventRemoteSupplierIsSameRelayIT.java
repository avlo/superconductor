package com.prosilion.superconductor.redis.service.event.curated;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.curated.BaseCacheCuratedBadgeDefinitionEventRemoteSupplierIsSameRelayMessageIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheCuratedDefinitionEventRemoteSupplierIsSameRelayIT extends BaseCacheCuratedBadgeDefinitionEventRemoteSupplierIsSameRelayMessageIT {
  @Autowired
  CacheCuratedDefinitionEventRemoteSupplierIsSameRelayIT(
     @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorRelayUrl, cacheServiceIF, superconductorInstanceIdentity);
  }
}
