package com.prosilion.superconductor.h2db;

import com.prosilion.superconductor.base.BaseMatchingReferencedEventIT;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingReferencedEventIT extends BaseMatchingReferencedEventIT {
  @Autowired
  MatchingReferencedEventIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      Duration requestTimeoutDuration) throws IOException {
    super(relayUrl, requestTimeoutDuration);
  }
}
