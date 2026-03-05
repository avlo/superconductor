package com.prosilion.superconductor.redis;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseBadgeAwardUpvoteEventMessageAuthIT;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "superconductor.auth.event.kinds=BADGE_AWARD_EVENT",
    "server.port=5557",
    "superconductor.relay.url=ws://localhost:5557",
    "spring.data.redis.port=6380"
})
public class BadgeAwardUpvoteEventMessageAuthIT extends BaseBadgeAwardUpvoteEventMessageAuthIT {
  @Autowired
  BadgeAwardUpvoteEventMessageAuthIT(
      @NonNull Identity superconductorInstanceIdentity,
      @NonNull @Value("${superconductor.relay.url}") String relayUri,
      Duration requestTimeoutDuration) throws NostrException {
    super(superconductorInstanceIdentity, relayUri, requestTimeoutDuration);
  }
}
