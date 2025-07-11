package com.prosilion.superconductor.service.clientresponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.message.NoticeMessage;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;

@Getter
public class ClientNoticeResponse implements ClientResponse {
  private final TextMessage textMessage;
  private final String sessionId;
  private final boolean valid;

  public ClientNoticeResponse(@NonNull String sessionId, @NonNull String noticeMessage, boolean valid) throws JsonProcessingException {
    this.sessionId = sessionId;
    this.textMessage = new TextMessage(new NoticeMessage(noticeMessage).encode());
    this.valid = valid;
  }
}
