package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.service.AbstractSubscriberService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.Filters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PersistentSubscriberService extends AbstractSubscriberService {
  private final SubscriberManager subscriberManager;
  private final SubscriberFiltersService subscriberFiltersService;

  @Autowired
  public PersistentSubscriberService(
      SubscriberManager subscriberManager,
      SubscriberFiltersService subscriberFiltersService,
      ApplicationEventPublisher publisher) {
    super(publisher);
    this.subscriberManager = subscriberManager;
    this.subscriberFiltersService = subscriberFiltersService;
  }

  @Override
  public Long save(@NonNull Subscriber subscriber, @NonNull List<Filters> filtersList) {
    try {
      removeSubscriberBySubscriberId(subscriber.getSubscriberId());
      log.info("removing matched subscriberId [{}], session [{}]", subscriber.getSubscriberId(), subscriber.getSessionId());
    } catch (NoExistingUserException e) {
      log.info("no match to remove for subscriberId [{}], session [{}]", subscriber.getSubscriberId(), subscriber.getSessionId());
    }
    log.info("saving subscriberId [{}], session [{}]", subscriber.getSubscriberId(), subscriber.getSessionId());
    Subscriber savedSubscriber = subscriberManager.save(subscriber);
    subscriberFiltersService.save(savedSubscriber.getId(), filtersList);
    return savedSubscriber.getId();
  }

  /**
   * For a given subscriber, removes all their active sessions.  Typically/Expected to occur on WebSocketSession close
   */
  @Override
  public List<Long> removeSubscriberBySessionId(@NonNull String sessionId) {
    subscriberManager.getBySessionId(sessionId).forEach(
        s -> subscriberFiltersService.deleteBySubscriberId(s.getId()));

    return subscriberManager.removeBySessionId(sessionId);
  }

  /**
   * For a given subscriber, removes a single session associated to a specific subscription.  Typically/Expected to occur on
   * a Nostr REQ CLOSE event
   */
  @Override
  public Long removeSubscriberBySubscriberId(@NonNull String subscriberId) throws NoExistingUserException {
    subscriberManager.getBySubscriberId(subscriberId)
        .ifPresent(
            subscriber ->
                subscriberFiltersService.deleteBySubscriberId(subscriber.getId()));

    return subscriberManager.removeBySubscriberId(subscriberId);
  }

//  public String deactivateSubscriberBySessionId(String sessionId) throws NoResultException {
//    Subscriber s = Optional.of/ofNullable(subscriberManager.getBySessionId(sessionId)).orElseThrow(NoResultException::new);
//    s.setActive(false);
//    return subscriberManager.save(s).getSubscriberId();
//  }

  @Override
  public List<Filters> getFiltersList(@NonNull Long subscriberId) {
    return subscriberFiltersService.getFiltersList(subscriberId);
  }

  @Override
  public Map<Long, List<Filters>> getAllFiltersOfAllSubscribers() {
    return subscriberFiltersService.getAllFiltersOfAllSubscribers();
  }

  @Override
  public Subscriber get(@NonNull Long subscriberId) {
    return subscriberManager.get(subscriberId).get();
  }
}
