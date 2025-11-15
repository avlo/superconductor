package com.prosilion.superconductor.redis;

import com.prosilion.superconductor.BaseMatchingMultipleGenericTagQuerySingleLetterIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedRedisStandalone
@ActiveProfiles("test")
class MatchingMultipleGenericTagQuerySingleLetterIT extends BaseMatchingMultipleGenericTagQuerySingleLetterIT {
  @Autowired
  MatchingMultipleGenericTagQuerySingleLetterIT(@NonNull @Value("${superconductor.relay.url}") String relayUrl) throws IOException {
    super(relayUrl);
  }
}
