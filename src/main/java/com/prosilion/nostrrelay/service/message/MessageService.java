package com.prosilion.nostrrelay.service.message;

import nostr.event.BaseMessage;

public interface MessageService<T extends BaseMessage> {
	void processIncoming(T t);
}
