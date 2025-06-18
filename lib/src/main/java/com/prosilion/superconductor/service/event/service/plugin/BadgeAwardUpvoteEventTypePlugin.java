package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.BadgeAwardUpvoteEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class BadgeAwardUpvoteEventTypePlugin<
    U extends Kind,
    V extends Type,
    T extends BadgeAwardUpvoteEvent<V>>
    implements AbstractEventTypePluginIF<U, V, T> {
  private static final Log log = LogFactory.getLog(BadgeAwardUpvoteEventTypePlugin.class);

  @Override
  public void processIncomingEvent(T event) {
    log.debug(String.format("processing incoming UPVOTE EVENT: [%s]", event.getKind()));
    event.doSomething();
  }

  @Override
  public U getKind() {
    return (U) Kind.BADGE_AWARD_EVENT;
  }

  @Override
  public V getType() {
    return (V) Type.UPVOTE;
  }
}
