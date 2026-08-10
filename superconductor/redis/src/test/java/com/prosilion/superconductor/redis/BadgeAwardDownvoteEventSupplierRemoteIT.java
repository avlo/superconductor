package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseBadgeAwardDownvoteEventSupplierRemoteIT;
import com.prosilion.superconductor.config.SingleContainerNonCuratedTestConfig;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@Import(SingleContainerNonCuratedTestConfig.class)
@TestPropertySource(properties = {
   "superconductor.event.curation.active=true"
})
public class BadgeAwardDownvoteEventSupplierRemoteIT extends BaseBadgeAwardDownvoteEventSupplierRemoteIT {
  @Autowired
  BadgeAwardDownvoteEventSupplierRemoteIT(
     @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl,
     @NonNull @Value("${superconductor.relay.url.two}") String superconductorRelayUrlTwo,
     @NonNull @Value("${superconductor.relay.url.three}") String superconductorRelayUrlThree,
     @NonNull Identity superconductorInstanceIdentity) throws NostrException {
    super(superconductorRelayUrl, superconductorRelayUrlTwo, superconductorRelayUrlThree, superconductorInstanceIdentity);
  }
}
