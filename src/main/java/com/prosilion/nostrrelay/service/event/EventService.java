package com.prosilion.nostrrelay.service.event;

import nostr.event.message.EventMessage;

import java.lang.reflect.InvocationTargetException;

public interface EventService<T extends EventMessage> {
  void processIncoming() throws InvocationTargetException, IllegalAccessException;
}
