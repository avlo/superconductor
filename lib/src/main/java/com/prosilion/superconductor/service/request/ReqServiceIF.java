package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.util.EmptyFiltersException;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.message.ReqMessage;

public interface ReqServiceIF<T extends GenericEventDtoIF> {
  <U extends ReqMessage> void processIncoming(@NonNull U reqMessage, @NonNull String sessionId) throws EmptyFiltersException;
}
