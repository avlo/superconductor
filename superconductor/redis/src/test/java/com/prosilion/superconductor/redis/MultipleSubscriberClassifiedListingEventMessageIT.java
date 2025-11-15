package com.prosilion.superconductor.redis;

import com.prosilion.superconductor.base.BaseMultipleSubscriberClassifiedListingEventMessageIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

@Slf4j
@EmbeddedRedisStandalone
@Nested
class MultipleSubscriberClassifiedListingEventMessageIT extends BaseMultipleSubscriberClassifiedListingEventMessageIT {
  @Autowired
  MultipleSubscriberClassifiedListingEventMessageIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(relayUrl, hexCounterSeed, hexNumberOfBytes, reqInstances);
  }
}
