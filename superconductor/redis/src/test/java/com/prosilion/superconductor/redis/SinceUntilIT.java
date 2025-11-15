package com.prosilion.superconductor.redis;

import com.prosilion.superconductor.BaseSinceUntilIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class SinceUntilIT extends BaseSinceUntilIT {
  @Autowired
  SinceUntilIT(@NonNull @Value("${superconductor.relay.url}") String relayUrl) throws IOException {
    super(relayUrl);
  }
}
