package com.prosilion.superconductor.base.service.message;

import com.prosilion.superconductor.base.util.NostrMediaType;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface RelayInfoDocServiceIF {
  static boolean isRelayInformationDocumentRequest(WebSocketSession session) {
    return session.getHandshakeHeaders().getAccept().stream().anyMatch(mediaType ->
        mediaType.equalsTypeAndSubtype(NostrMediaType.APPLICATION_NOSTR_JSON));
  }

  WebSocketMessage<String> processIncoming();
}
