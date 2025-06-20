package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.util.EmptyFiltersException;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.message.ReqMessage;

public interface ReqServiceIF<T extends GenericEventKindIF> {
  <U extends ReqMessage> void processIncoming(@NonNull U reqMessage, @NonNull String sessionId) throws EmptyFiltersException;
}
