package com.prosilion.superconductor.h2db;

import com.prosilion.superconductor.base.BaseMultipleZapRequestEventMessageIT;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

@Slf4j
@Nested
class MultipleZapRequestEventMessageIT extends BaseMultipleZapRequestEventMessageIT {
  @Autowired
  MultipleZapRequestEventMessageIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      Duration requestTimeoutDuration) {
    super(relayUrl, hexCounterSeed, hexNumberOfBytes, reqInstances,  requestTimeoutDuration);
  }
}
