package com.prosilion.superconductor.base.service.request;

import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.base.util.EmptyFiltersException;
import org.springframework.lang.NonNull;

public interface ReqServiceIF {
  void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) throws EmptyFiltersException;
}
