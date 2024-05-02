package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.pubsub.AddSubscriberEvent;
import jakarta.persistence.NoResultException;
import nostr.event.list.FiltersList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * {@link AddSubscriberEvent} is registered & used by {@link SubscriberService}
     */
    publisher.publishEvent(new AddSubscriberEvent(savedSubscriber));   //Notify the listeners
  }

  public Subscriber get(Long subscriberId) {
    return subscriberManager.get(subscriberId).get();
  }

  public List<Long> removeSubscriberBySessionId(String sessionId) {
    List<Subscriber> subscribers = Optional.of(subscriberManager.getBySessionId(sessionId)).orElseThrow(NoResultException::new);
    subscribers.forEach(s -> subscriberFiltersService.deleteBySubscriberId(s.getId()));
    return subscriberManager.removeBySessionId(sessionId);
  }

  public Long removeSubscriberBySubscriberId(String subscriberId) {
    Subscriber s = Optional.of(subscriberManager.getBySubscriberId(subscriberId)).orElseThrow(NoResultException::new);
    subscriberFiltersService.deleteBySubscriberId(s.getId());
    return subscriberManager.removeBySubscriberId(subscriberId);
  }

//  public String deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    Subscriber s = Optional.of(subscriberManager.getBySessionId(sessionId)).orElseThrow(NoResultException::new);
//    s.setActive(false);
//    return subscriberManager.save(s).getSubscriberId();
//  }
}
