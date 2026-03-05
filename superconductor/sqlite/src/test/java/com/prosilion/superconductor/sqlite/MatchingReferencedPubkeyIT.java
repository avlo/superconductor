package com.prosilion.superconductor.sqlite;

import com.prosilion.superconductor.base.BaseMatchingReferencedPubkeyIT;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
class MatchingReferencedPubkeyIT extends BaseMatchingReferencedPubkeyIT {
  @Autowired
  MatchingReferencedPubkeyIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      Duration requestTimeoutDuration) throws IOException {
    super(relayUrl, requestTimeoutDuration);
  }
}
