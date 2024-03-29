package com.prosilion.nostrrelay.service.event;

import nostr.base.IEvent;
import nostr.event.message.EventMessage;

import java.lang.reflect.InvocationTargetException;

public interface EventService<T extends EventMessage> {
  IEvent processIncoming() throws InvocationTargetException, IllegalAccessException;
}
