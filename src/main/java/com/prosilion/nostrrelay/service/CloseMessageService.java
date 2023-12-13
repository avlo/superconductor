package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.event.message.CloseMessage;

import java.util.logging.Level;

@Log
public class CloseMessageService implements MessageService {
  private final CloseMessage closeMessage;

  public CloseMessageService(CloseMessage closeMessage) {
    log.log(Level.INFO, "CLOSE service initiated");
    this.closeMessage = closeMessage;
  }

  @Override
  public CloseMessage processIncoming() {
    log.log(Level.INFO, "processing CLOSE event");
    return new CloseMessage(closeMessage.getSubscriptionId());
  }
}
