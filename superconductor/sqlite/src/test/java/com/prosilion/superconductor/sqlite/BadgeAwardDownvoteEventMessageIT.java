package com.prosilion.superconductor.sqlite;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseBadgeAwardDownvoteEventMessageIT;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
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
public class BadgeAwardDownvoteEventMessageIT extends BaseBadgeAwardDownvoteEventMessageIT {
  @Autowired
  BadgeAwardDownvoteEventMessageIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull Identity superconductorInstanceIdentity,
      Duration requestTimeoutDuration) throws IOException, NostrException {
    super(relayUrl, cacheServiceIF, superconductorInstanceIdentity, requestTimeoutDuration);
  }
}
