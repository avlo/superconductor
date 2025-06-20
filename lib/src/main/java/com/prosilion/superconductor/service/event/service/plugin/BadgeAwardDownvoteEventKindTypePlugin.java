package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class BadgeAwardDownvoteEventKindTypePlugin implements AbstractEventKindTypePluginIF {
  private static final Log log = LogFactory.getLog(BadgeAwardDownvoteEventKindTypePlugin.class);

  @Override
  public void processIncomingEvent(GenericEventKindTypeIF event) {
    log.debug(String.format("processing incoming DOWNVOTE EVENT: [%s]", event.getKind()));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }

  @Override
  public KindType getKindType() {
    return KindType.DOWNVOTE;
  }
}
