package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.pubsub.AddSubscriberEvent;
import com.prosilion.nostrrelay.service.SubscriberSessionPool;
import jakarta.persistence.NoResultException;
import nostr.event.list.FiltersList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriberService {
  private final SubscriberManager subscriberManager;
  private final SubscriberFiltersService subscriberFiltersService;
  private final ApplicationEventPublisher publisher;

  public SubscriberService(
      SubscriberManager subscriberManager,
      SubscriberFiltersService subscriberFiltersService,
      ApplicationEventPublisher publisher) {
    this.subscriberManager = subscriberManager;
    this.subscriberFiltersService = subscriberFiltersService;
    this.publisher = publisher;
  }

  public void save(Subscriber subscriber, FiltersList filtersList) {
    Subscriber savedSubscriber = Optional.of(
            subscriberManager.save(subscriber))
        .orElseThrow(NoResultException::new);
    // TODO: below add might also suffice for update?
    subscriberFiltersService.save(savedSubscriber.getId(), filtersList);

    /**
     * {@link AddSubscriberEvent} is registered & used by
     * {@link SubscriberSessionPool} (not EventNotifierEngine)
     */
    publisher.publishEvent(new AddSubscriberEvent(savedSubscriber));   //Notify the listeners
  }

  public Subscriber get(Long subscriberId) {
    return subscriberManager.get(subscriberId).get();
  }

  public void removeSubscriberBySessionId(String sessionId) {
    subscriberManager.removeBySessionId(sessionId);
  }

  public void deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
    Subscriber s = Optional.of(subscriberManager.getBySessionId(sessionId)).orElseThrow(NoResultException::new);
    s.setActive(false);
    subscriberManager.save(s);
  }
}
