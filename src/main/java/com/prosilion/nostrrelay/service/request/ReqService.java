package com.prosilion.nostrrelay.service.request;

import nostr.base.IEvent;
import nostr.event.message.ReqMessage;

public interface ReqService<T extends ReqMessage> {
  T processIncoming();
}
