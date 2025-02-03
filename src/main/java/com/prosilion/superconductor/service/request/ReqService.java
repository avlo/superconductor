package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.util.EmptyFiltersException;
import lombok.NonNull;
import nostr.event.filter.FiltersCore;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReqService<T extends ReqMessage, U extends GenericEvent> {
  private final AbstractSubscriberService abstractSubscriberService;
  private final NotifierService<U> notifierService;

  @Autowired
  public ReqService(AbstractSubscriberService abstractSubscriberService, NotifierService<U> notifierService) {
    this.abstractSubscriberService = abstractSubscriberService;
    this.notifierService = notifierService;
  }

  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) throws EmptyFiltersException {
    notifierService.subscriptionEventHandler(
        abstractSubscriberService.save(
            new Subscriber(
                reqMessage.getSubscriptionId(),
                sessionId,
                true),
            getFiltersCoreList(reqMessage)
        ));
  }

  private static <T extends ReqMessage> @NotNull List<FiltersCore> getFiltersCoreList(@NotNull T reqMessage) {
    List<Filters> filtersList = reqMessage.getFiltersList();
    return filtersList.stream().map(filters -> getFiltersCore(filters)).toList();
  }

  private static FiltersCore getFiltersCore(Filters filters) {
    return filters.getFiltersCore();
  }
}
