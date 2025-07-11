package com.prosilion.superconductor.base.service.clientresponse;

import org.springframework.web.socket.TextMessage;

public interface ClientResponse {
  TextMessage getTextMessage();
  String getSessionId();
  boolean isValid();
}
