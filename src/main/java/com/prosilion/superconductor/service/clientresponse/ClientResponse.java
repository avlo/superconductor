package com.prosilion.superconductor.service.clientresponse;

import org.springframework.web.socket.TextMessage;

public interface ClientResponse {
  TextMessage getTextMessage();
  String getSessionId();
  boolean isValid();
}
