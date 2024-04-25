package com.prosilion.nostrrelay.service.request;

import nostr.event.message.ReqMessage;

public interface ReqServiceIF<T extends ReqMessage> {
	void processIncoming(T reqMessage, String sessionId);
}
