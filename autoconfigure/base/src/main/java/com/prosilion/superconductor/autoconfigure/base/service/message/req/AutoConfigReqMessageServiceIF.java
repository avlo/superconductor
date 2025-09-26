package com.prosilion.superconductor.autoconfigure.base.service.message.req;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.base.service.message.MessageServiceIF;
import org.springframework.lang.NonNull;

public interface AutoConfigReqMessageServiceIF extends MessageServiceIF<ReqMessage> {
  void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId);
  default Command getCommand() {
    return Command.REQ;
  }
}
