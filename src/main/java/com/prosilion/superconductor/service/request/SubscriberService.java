package com.prosilion.superconductor.service.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.pubsub.FireNostrEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.api.NIP01;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SubscriberService {
  private final SubscriberManager subscriberManager;
  private final SubscriberFiltersService subscriberFiltersService;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public SubscriberService(
      SubscriberManager subscriberManager,
      SubscriberFiltersService subscriberFiltersService,
      ApplicationEventPublisher publisher) {
    this.subscriberManager = subscriberManager;
    this.subscriberFiltersService = subscriberFiltersService;
    this.publisher = publisher;
  }

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
  public List<Long> removeSubscriberBySessionId(@NonNull String sessionId) {
    subscriberManager.getBySessionId(sessionId).forEach(
        s -> subscriberFiltersService.deleteBySubscriberId(s.getId()));

    return subscriberManager.removeBySessionId(sessionId);
  }

  /**
   * For a given subscriber, removes a single session associated to a specific subscription.  Typically/Expected to occur on
   * a Nostr REQ CLOSE event
   */
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

  public List<Filters> getFiltersList(@NonNull Long subscriberId) {
    return subscriberFiltersService.getFiltersList(subscriberId);
  }

  public Map<Long, List<Filters>> getAllFiltersOfAllSubscribers() {
    return subscriberFiltersService.getAllFiltersOfAllSubscribers();
  }

  public <T extends GenericEvent> void broadcastToClients(@NonNull FireNostrEvent<T> fireNostrEvent) throws JsonProcessingException {
    EventMessage message = NIP01.createEventMessage(fireNostrEvent.event(), String.valueOf(fireNostrEvent.subscriberId()));
    BroadcastMessageEvent<EventMessage> event = new BroadcastMessageEvent<>(get(fireNostrEvent.subscriberId()).getSessionId(), message);
    publisher.publishEvent(event);
  }

  private Subscriber get(@NonNull Long subscriberId) {
    return subscriberManager.get(subscriberId).get();
  }
}
