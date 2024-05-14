package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import nostr.event.message.ReqMessage;
import org.springframework.stereotype.Service;

@Getter
@Service
public class ReqService<T extends ReqMessage> {
  private final SubscriberService subscriberService;

  public ReqService(SubscriberService subscriberService) {
    this.subscriberService = subscriberService;
  }

  public void processIncoming(@NotNull T reqMessage, String sessionId) {
    subscriberService.save(
        new Subscriber(
            reqMessage.getSubscriptionId(),
            sessionId,
            true),
        reqMessage.getFiltersList()
    );
  }
}
