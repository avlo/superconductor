package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class BadgeAwardDownvoteEventTypePlugin<
    U extends Kind,
    T extends GenericEventKindTypeIF>
    implements AbstractEventTypePluginIF<U, KindType, T> {
  private static final Log log = LogFactory.getLog(BadgeAwardDownvoteEventTypePlugin.class);

  @Override
  public void processIncomingEvent(T event) {
    log.debug(String.format("processing incoming DOWNVOTE EVENT: [%s]", event.getKind()));
  }

  @Override
  public U getKind() {
    return (U) Kind.BADGE_AWARD_EVENT;
  }

  @Override
  public KindType getKindType() {
    return KindType.DOWNVOTE;
  }
}
