package com.prosilion.superconductor.service.message.req;

import lombok.NonNull;
import nostr.event.message.ReqMessage;

public interface ReqMessageServiceBean<T extends ReqMessage> {
  void processIncoming(T reqMessage, @NonNull String sessionId);

  void processNoticeClientResponse(T reqMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
