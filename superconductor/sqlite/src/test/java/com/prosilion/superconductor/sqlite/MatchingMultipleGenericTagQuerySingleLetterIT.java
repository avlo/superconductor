package com.prosilion.superconductor.sqlite;

import com.prosilion.superconductor.base.BaseMatchingMultipleGenericTagQuerySingleLetterIT;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MatchingMultipleGenericTagQuerySingleLetterIT extends BaseMatchingMultipleGenericTagQuerySingleLetterIT {
  @Autowired
  MatchingMultipleGenericTagQuerySingleLetterIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      Duration requestTimeoutDuration) throws IOException {
    super(relayUrl, requestTimeoutDuration);
  }
}
