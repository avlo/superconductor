package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriberManager {
  private final SubscriberRepository subscriberRepository;

  public SubscriberManager(SubscriberRepository subscriberRepository) {
    this.subscriberRepository = subscriberRepository;
  }

  public Subscriber save(Subscriber subscriberToSave) {
    return subscriberRepository.save(subscriberToSave);
  }

  public Optional<Subscriber> get(Long id) {
    return Optional.of(subscriberRepository.findById(id)).orElse(Optional.empty());
  }

  public Optional<Subscriber> getBySubscriberId(String subscriberId) {
    return Optional.of(subscriberRepository.findBySubscriberId(subscriberId)).orElse(Optional.empty());
  }

  public Optional<List<Subscriber>> getBySessionId(String sessionId) {
    return Optional.of(subscriberRepository.findAllBySessionId(sessionId)).orElse(Optional.empty());
  }

  public List<Long> removeBySessionId(String sessionId) {
    List<Subscriber> subscribers = getBySessionId(sessionId).orElse(Collections.emptyList());
    subscribers.forEach(s -> subscriberRepository.deleteById(s.getId()));
    return subscribers.stream().map(Subscriber::getId).toList();
  }

  public Long removeBySubscriberId(String subscriberId) throws NoExistingUserException {
    Subscriber subscriber = getBySubscriberId(subscriberId).orElseThrow(NoExistingUserException::new);
    subscriberRepository.deleteById(subscriber.getId());
    return subscriber.getId();
  }
}
