package com.prosilion.superconductor.redis.service.event;

import com.ezylang.evalex.parser.ParseException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseCacheCuratedBadgeAwardGenericEventServiceIT;
import com.prosilion.superconductor.base.cache.CacheCuratedBadgeAwardEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.time.Duration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class CacheCuratedBadgeAwardGenericEventServiceIT extends BaseCacheCuratedBadgeAwardGenericEventServiceIT {

  @Autowired
  public CacheCuratedBadgeAwardGenericEventServiceIT(
     @Value("${superconductor.relay.url}") String relayUrl,
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull @Qualifier("cacheCurationSetsEventService") CacheCuratedBadgeAwardEventServiceIF cacheCuratedBadgeAwardEventServiceIF,
     Duration requestTimeoutDuration) throws ParseException {
    super(relayUrl, superconductorInstanceIdentity, cacheServiceIF, cacheCuratedBadgeAwardEventServiceIF, requestTimeoutDuration);
  }
}
