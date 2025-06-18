package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.Type;
import com.prosilion.nostr.event.BadgeAwardDownvoteEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class BadgeAwardDownvoteEventTypePlugin<
    U extends Kind,
    V extends Type,
    T extends BadgeAwardDownvoteEvent<V>>
    implements AbstractEventTypePluginIF<U, V, T> {
  private static final Log log = LogFactory.getLog(BadgeAwardDownvoteEventTypePlugin.class);

  @Override
  public void processIncomingEvent(T event) {
    log.debug(String.format("processing incoming DOWNVOTE EVENT: [%s]", event.getKind()));
    event.doSomething();
  }

  @Override
  public U getKind() {
    return (U) Kind.BADGE_AWARD_EVENT;
  }

  @Override
  public V getType() {
    return (V) Type.DOWNVOTE;
  }
}
