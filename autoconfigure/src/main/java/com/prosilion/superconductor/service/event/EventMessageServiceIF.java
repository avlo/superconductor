package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.service.message.MessageService;
import nostr.event.message.EventMessage;

public interface EventMessageServiceIF<T extends EventMessage> extends MessageService<T> {
//  void processOkClientResponse(T eventMessage, @NonNull String sessionId);
//  void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage);
}
