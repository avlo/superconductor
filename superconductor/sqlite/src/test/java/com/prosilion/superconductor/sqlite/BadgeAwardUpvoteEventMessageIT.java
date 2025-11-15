package com.prosilion.superconductor.sqlite;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.BaseBadgeAwardUpvoteEventMessageIT;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class BadgeAwardUpvoteEventMessageIT extends BaseBadgeAwardUpvoteEventMessageIT {
  @Autowired
  BadgeAwardUpvoteEventMessageIT(
      @NonNull @Value("${superconductor.relay.url}") String relayUrl,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull Identity superconductorInstanceIdentity) throws IOException, NostrException {
    super(relayUrl, badgeDefinitionUpvoteEvent, superconductorInstanceIdentity);
  }
}
