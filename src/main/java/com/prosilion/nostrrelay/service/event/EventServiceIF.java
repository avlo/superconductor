package com.prosilion.nostrrelay.service.event;

import nostr.event.message.EventMessage;

import java.lang.reflect.InvocationTargetException;

public interface EventServiceIF<T extends EventMessage> {
	void processIncoming(T eventMessage) throws InvocationTargetException, IllegalAccessException;
}
