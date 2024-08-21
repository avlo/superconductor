package com.prosilion.superconductor.service.okresponse;

import org.springframework.web.socket.TextMessage;

public interface ClientResponse {
  TextMessage getTextMessage();
  String getSessionId();
  boolean isValid();
}
