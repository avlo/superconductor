package com.prosilion.superconductor.autoconfigure.base.service.message.req;

import com.prosilion.nostr.message.ReqMessage;
import org.springframework.lang.NonNull;

public interface ReqMessageServiceIF {
  void processIncoming(ReqMessage reqMessage, @NonNull String sessionId);

  void processNoticeClientResponse(ReqMessage reqMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
