package com.prosilion.nostrrelay.service.request;

import jakarta.websocket.Session;
import nostr.base.IEvent;
import nostr.event.message.ReqMessage;

public interface ReqService<T extends ReqMessage> {
  T processIncoming(Session session);
}
