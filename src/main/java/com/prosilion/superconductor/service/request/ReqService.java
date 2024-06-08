package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.service.NotifierService;
import jakarta.validation.constraints.NotNull;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.springframework.stereotype.Service;

@Service
public class ReqService<T extends ReqMessage, U extends GenericEvent> {
  private final SubscriberService subscriberService;
  private final NotifierService<U> notifierService;

  public ReqService(SubscriberService subscriberService, NotifierService<U> notifierService) {
    this.subscriberService = subscriberService;
    this.notifierService = notifierService;
  }

  public void processIncoming(@NotNull T reqMessage, String sessionId) {
    Long savedSubscriberId = subscriberService.save(
        new Subscriber(
            reqMessage.getSubscriptionId(),
            sessionId,
            true),
        reqMessage.getFiltersList()
    );
    notifierService.subscriptionEventHandler(savedSubscriberId);
  }
}
