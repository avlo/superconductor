package com.prosilion.superconductor.supplier.local;

import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.supplier.local.abstracts.AbstractBadgeAwardReputationEventMessageSupplierLocalListIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class BadgeAwardReputationEventMessageSupplierLocalListIT extends AbstractBadgeAwardReputationEventMessageSupplierLocalListIT {

  @Autowired
  public BadgeAwardReputationEventMessageSupplierLocalListIT(
     @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(superconductorInstanceIdentity, superconductorRelayUrl, superconductorRelayUrl, cacheServiceIF);
  }
}
