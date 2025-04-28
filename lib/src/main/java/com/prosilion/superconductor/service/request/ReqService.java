package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.util.EmptyFiltersException;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReqService<T extends GenericEvent> {
  private final AbstractSubscriberService abstractSubscriberService;
  private final NotifierService<T> notifierService;

  @Autowired
  public ReqService(AbstractSubscriberService abstractSubscriberService, NotifierService<T> notifierService) {
    this.abstractSubscriberService = abstractSubscriberService;
    this.notifierService = notifierService;
  }

  public <U extends ReqMessage> void processIncoming(@NonNull U reqMessage, @NonNull String sessionId) throws EmptyFiltersException {
    notifierService.subscriptionEventHandler(
        abstractSubscriberService.save(
            new Subscriber(
                reqMessage.getSubscriptionId(),
                sessionId,
                true),
            reqMessage.getFiltersList()));
  }
}
