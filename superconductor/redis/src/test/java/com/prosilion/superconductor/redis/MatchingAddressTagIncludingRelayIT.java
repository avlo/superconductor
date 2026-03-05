package com.prosilion.superconductor.redis;

import com.prosilion.superconductor.base.BaseMatchingAddressTagIncludingRelayIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingAddressTagIncludingRelayIT extends BaseMatchingAddressTagIncludingRelayIT {
  @Autowired
  MatchingAddressTagIncludingRelayIT(@NonNull String relayUrl, Duration requestTimeoutDuration) throws IOException {
    super(relayUrl, requestTimeoutDuration);
  }
}
