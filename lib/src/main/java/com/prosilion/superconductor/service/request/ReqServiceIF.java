package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.util.EmptyFiltersException;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;

public interface ReqServiceIF<T extends GenericEvent> {
  <U extends ReqMessage> void processIncoming(@NonNull U reqMessage, @NonNull String sessionId) throws EmptyFiltersException;
}
