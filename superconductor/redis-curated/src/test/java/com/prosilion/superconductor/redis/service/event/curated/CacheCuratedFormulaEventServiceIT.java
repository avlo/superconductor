package com.prosilion.superconductor.redis.service.event.curated;

import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.autoconfigure.base.service.event.curated.CacheCuratedFormulaEventService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.curated.BaseCacheCuratedFormulaEventServiceIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheCuratedFormulaEventServiceIT extends BaseCacheCuratedFormulaEventServiceIT {
  @Autowired
  public CacheCuratedFormulaEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull CacheCuratedFormulaEventService cacheCuratedFormulaEventService) {
    super(
       relayUrl,
       superconductorInstanceIdentity,
       cacheServiceIF,
       cacheCuratedFormulaEventService);
  }
}
