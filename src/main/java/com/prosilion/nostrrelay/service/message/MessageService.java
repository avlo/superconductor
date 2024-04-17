package com.prosilion.nostrrelay.service.message;

import nostr.event.BaseMessage;

import java.lang.reflect.InvocationTargetException;

public interface MessageService<T extends BaseMessage> {
  T processIncoming(String sessionId) throws InvocationTargetException, IllegalAccessException;
}
