package com.prosilion.superconductor.redis;

import com.prosilion.superconductor.base.BaseMatchingReferencedPubkeyIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@ActiveProfiles("test")
class MatchingReferencedPubkeyIT extends BaseMatchingReferencedPubkeyIT {
  @Autowired
  MatchingReferencedPubkeyIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      Duration requestTimeoutDuration) throws IOException {
    super(relayUrl, requestTimeoutDuration);
  }
}
