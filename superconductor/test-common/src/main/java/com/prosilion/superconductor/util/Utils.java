package com.prosilion.superconductor.util;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import java.util.List;

public class Utils {
  public static List<EventIF> getEventIFs(List<BaseMessage> returnedBaseMessages) {
    return returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .toList();
  }
}
