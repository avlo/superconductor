package com.prosilion.nostrrelay.service.message;

import jakarta.websocket.Session;
import nostr.event.BaseMessage;

import java.lang.reflect.InvocationTargetException;

public interface MessageService<T extends BaseMessage> {
  T processIncoming(Session session) throws InvocationTargetException, IllegalAccessException;
}
