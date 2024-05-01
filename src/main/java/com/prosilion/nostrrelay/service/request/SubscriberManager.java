package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.repository.SubscriberRepository;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriberManager {
  private final SubscriberRepository subscriberRepository;

  public SubscriberManager(SubscriberRepository subscriberRepository) {
    this.subscriberRepository = subscriberRepository;
  }

  public Subscriber save(Subscriber subscriberToSave) {
//    TODO: stream below
    try {
      // if subscriber already exists
      Subscriber matchingSubscriber = getBySubscriberId(subscriberToSave.getSubscriberId());
      subscriberToSave.setId(matchingSubscriber.getId());
    } catch (NoResultException e) {
      // if subscriber doesn't exist
    }
    return subscriberRepository.save(subscriberToSave);
  }

  public Optional<Subscriber> get(Long id) {
    return Optional.of(subscriberRepository.findById(id)).orElseThrow(NoResultException::new);
  }

  public Subscriber getBySubscriberId(String subscriberId) {
    return subscriberRepository.findBySubscriberId(subscriberId).orElseThrow(NoResultException::new);
  }

  public Subscriber getBySessionId(String sessionId) {
    return subscriberRepository.findBySessionId(sessionId).orElseThrow(NoResultException::new);
  }

  public Long removeBySessionId(String sessionId) {
    Subscriber s = getBySessionId(sessionId);
    subscriberRepository.deleteById(s.getId());
    return s.getId();
  }
}
