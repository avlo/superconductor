package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseBadgeAwardUpvoteEventMessageIT;
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
public class BadgeAwardUpvoteEventMessageIT extends BaseBadgeAwardUpvoteEventMessageIT {
  @Autowired
  BadgeAwardUpvoteEventMessageIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      @NonNull Identity superconductorInstanceIdentity) throws IOException, NostrException {
    super(relayUrl, superconductorInstanceIdentity);
  }
}
