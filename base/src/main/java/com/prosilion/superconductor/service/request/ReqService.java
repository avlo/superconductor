package com.prosilion.superconductor.service.request;

import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.util.EmptyFiltersException;
import org.springframework.lang.NonNull;

public class ReqService implements ReqServiceIF {
  private final AbstractSubscriberService abstractSubscriberService;
  private final NotifierService notifierService;

  public ReqService(@NonNull AbstractSubscriberService abstractSubscriberService, @NonNull NotifierService notifierService) {
    this.abstractSubscriberService = abstractSubscriberService;
    this.notifierService = notifierService;
  }

  @Override
  public void processIncoming(@NonNull ReqMessage reqMessage, @NonNull String sessionId) throws EmptyFiltersException {
    notifierService.subscriptionEventHandler(
        abstractSubscriberService.save(
            new Subscriber(
                reqMessage.getSubscriptionId(),
                sessionId,
                true),
            reqMessage.getFiltersList()));
  }
}
