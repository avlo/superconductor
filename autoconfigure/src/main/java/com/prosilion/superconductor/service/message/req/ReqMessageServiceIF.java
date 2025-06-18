package com.prosilion.superconductor.service.message.req;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.message.ReqMessage;

public interface ReqMessageServiceIF<T extends ReqMessage> {
  void processIncoming(T reqMessage, @NonNull String sessionId);

  void processNoticeClientResponse(T reqMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
