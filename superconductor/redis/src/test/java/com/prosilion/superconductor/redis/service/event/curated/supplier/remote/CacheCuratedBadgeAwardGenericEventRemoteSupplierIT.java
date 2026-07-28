package com.prosilion.superconductor.redis.service.event.curated.supplier.remote;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.curated.supplier.remote.BaseCacheCuratedBadgeAwardEventRemoteSupplierMessageIT;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.redis.config.SingleContainerTestConfig;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@Import(SingleContainerTestConfig.class)
public class CacheCuratedBadgeAwardGenericEventRemoteSupplierIT extends BaseCacheCuratedBadgeAwardEventRemoteSupplierMessageIT {
  @Autowired
  CacheCuratedBadgeAwardGenericEventRemoteSupplierIT(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
     @NonNull @Value("${superconductor.relay.url.two}") String superconductorRelayUrlTwo,
     @NonNull @Value("${superconductor.relay.url.three}") String superconductorRelayUrlThree) throws IOException, NostrException {
    super(superconductorInstanceIdentity, cacheServiceIF, superconductorRelayUrl, superconductorRelayUrlTwo, superconductorRelayUrlThree);
  }
}
