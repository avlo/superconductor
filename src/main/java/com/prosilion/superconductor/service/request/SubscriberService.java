package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.pubsub.RemoveSubscriberFilterEvent;
import lombok.extern.slf4j.Slf4j;
import nostr.event.list.FiltersList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
    try {
      publisher.publishEvent(new RemoveSubscriberFilterEvent(removeSubscriberBySubscriberId(subscriber.getSubscriberId())));
      log.info("removing matched subscriberId [{}], session [{}]", subscriber.getSubscriberId(), subscriber.getSessionId());
    } catch (NoExistingUserException e) {
      log.info("no match to remove for subscriberId [{}], session [{}]", subscriber.getSubscriberId(), subscriber.getSessionId());
    }
    log.info("saving subscriberId [{}], session [{}]", subscriber.getSubscriberId(), subscriber.getSessionId());
    Subscriber savedSubscriber = subscriberManager.save(subscriber);
    subscriberFiltersService.save(savedSubscriber.getId(), filtersList);
  }

  public Subscriber get(Long subscriberId) {
    return subscriberManager.get(subscriberId).get();
  }

  /**
   * For a given subscriber, removes all their active sessions.  Typically/Expected to occur on WebSocketSession close
   */
  public List<Long> removeSubscriberBySessionId(String sessionId) {
    subscriberManager.getBySessionId(sessionId)
        .ifPresent(
            subscribers -> subscribers.forEach(
                s -> subscriberFiltersService.deleteBySubscriberId(s.getId())));
    return subscriberManager.removeBySessionId(sessionId);
  }

  /**
   * For a given subscriber, removes a single session associated to a specific subscription.  Typically/Expected to occur on
   * a Nostr REQ CLOSE event
   */
  public Long removeSubscriberBySubscriberId(String subscriberId) throws NoExistingUserException {
    subscriberManager.getBySubscriberId(subscriberId)
        .ifPresent(
            subscriber ->
                subscriberFiltersService.deleteBySubscriberId(subscriber.getId()));

    return subscriberManager.removeBySubscriberId(subscriberId);
  }

//  public String deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    Subscriber s = Optional.of(subscriberManager.getBySessionId(sessionId)).orElseThrow(NoResultException::new);
//    s.setActive(false);
//    return subscriberManager.save(s).getSubscriberId();
//  }
}
