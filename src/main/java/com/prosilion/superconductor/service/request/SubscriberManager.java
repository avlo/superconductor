package com.prosilion.superconductor.service.request;

import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.repository.SubscriberRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriberManager {
  private final SubscriberRepository subscriberRepository;

  @Autowired
  public SubscriberManager(SubscriberRepository subscriberRepository) {
    this.subscriberRepository = subscriberRepository;
  }

  public Subscriber save(@NonNull Subscriber subscriberToSave) {
    return subscriberRepository.save(subscriberToSave);
  }

  public Optional<Subscriber> get(@NonNull Long id) {
    return Optional.of(subscriberRepository.findById(id)).orElse(Optional.empty());
  }

  public Optional<Subscriber> getBySubscriberId(@NonNull String subscriberId) {
    return Optional.of(subscriberRepository.findBySubscriberId(subscriberId)).orElse(Optional.empty());
  }

  public List<Subscriber> getBySessionId(@NonNull String sessionId) {
    return subscriberRepository.findAllBySessionId(sessionId).stream().toList();
  }

  public List<Long> removeBySessionId(@NonNull String sessionId) {
    List<Subscriber> subscribers = getBySessionId(sessionId);
    subscribers.forEach(s -> subscriberRepository.deleteById(s.getId()));
    return subscribers.stream().map(Subscriber::getId).toList();
  }

  public Long removeBySubscriberId(@NonNull String subscriberId) throws NoExistingUserException {
    Subscriber subscriber = getBySubscriberId(subscriberId).orElseThrow(NoExistingUserException::new);
    subscriberRepository.deleteById(subscriber.getId());
    return subscriber.getId();
  }
}
