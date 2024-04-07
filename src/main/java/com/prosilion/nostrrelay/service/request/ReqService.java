package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.service.filters.SubscriberFiltersService;
import com.prosilion.nostrrelay.service.filters.SubscriberService;
import jakarta.websocket.Session;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.list.FiltersList;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;

@Log
@Getter
public class ReqService<T extends ReqMessage> implements ReqServiceIF<T> {
  private final SubscriberService subscriberService;
  private final SubscriberFiltersService subscriberFiltersService;
  private final FiltersList filtersList;
  private final String subId;

  public ReqService(@NotNull T reqMessage) {
    subscriberFiltersService = ApplicationContextProvider.getApplicationContext().getBean(SubscriberFiltersService.class);
    subscriberService = ApplicationContextProvider.getApplicationContext().getBean(SubscriberService.class);
    this.filtersList = reqMessage.getFiltersList();
    this.subId = reqMessage.getSubscriptionId();
  }

  public T processIncoming(Session session) {
    Subscriber subscriber = new Subscriber(subId, session.getId());
    subscriberService.save(subscriber);
    subscriberFiltersService.processFilters(subscriber, filtersList);
    return null;
  }
}
