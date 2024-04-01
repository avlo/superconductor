package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.service.filters.FiltersServiceImpl;
import com.prosilion.nostrrelay.service.filters.SubscriberServiceImpl;
import jakarta.websocket.Session;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;

import java.security.Principal;

@Log
@Getter
public class ReqServiceImpl<T extends ReqMessage> implements ReqService<T> {
  private String subId;
  private SubscriberServiceImpl subscriberService;
  private FiltersServiceImpl filtersService;

  public ReqServiceImpl(@NotNull T reqMessage) {
    filtersService = new FiltersServiceImpl(reqMessage.getFiltersList());
    subscriberService = ApplicationContextProvider.getApplicationContext().getBean(SubscriberServiceImpl.class);
    subId = reqMessage.getSubscriptionId();
  }

  public T processIncoming(Session session) {
    Subscriber subscriber = new Subscriber(subId, session.getId());
    subscriberService.save(subscriber);
    filtersService.processFilters(subscriber);
    return null;
  }
}
