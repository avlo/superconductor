package com.prosilion.nostrrelay.service.request;

import jakarta.websocket.Session;
import nostr.event.message.ReqMessage;

public interface ReqServiceIF<T extends ReqMessage> {
  T processIncoming(Session session);
}
