package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.repository.SubscriberRepository;
import jakarta.persistence.NoResultException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
@Service
public class SubscriberManager {
  private final SubscriberRepository subscriberRepository;

  public SubscriberManager(SubscriberRepository subscriberRepository) {
    this.subscriberRepository = subscriberRepository;
  }

  public Subscriber save(Subscriber subscriberToSave) {
    try {
      // if subscriber already exists
      subscriberToSave.setId(getBySubscriberId(subscriberToSave.getSubscriberId()).getId());
      log.log(Level.INFO, "updating existing subscriber id [{0}]", subscriberToSave.getSubscriberId());
    } catch (NoResultException e) {
      // if subscriber doesn't exist
      log.log(Level.INFO, "saving new subscriber id [{0}]", subscriberToSave.getSubscriberId());
    }
    return subscriberRepository.save(subscriberToSave);
  }

  public Optional<Subscriber> get(Long id) {
    return Optional.of(subscriberRepository.findById(id)).orElseThrow(NoResultException::new);
  }

  public Subscriber getBySubscriberId(String subscriberId) {
    return subscriberRepository.findBySubscriberId(subscriberId).orElseThrow(NoResultException::new);
  }

  public List<Subscriber> getBySessionId(String sessionId) {
    return subscriberRepository.findBySessionId(sessionId).orElseThrow(NoResultException::new);
  }

  public List<Long> removeBySessionId(String sessionId) {
    List<Subscriber> subscribers = getBySessionId(sessionId);
    subscribers.forEach(s -> subscriberRepository.deleteById(s.getId()));
    return subscribers.stream().map(Subscriber::getId).toList();
  }

  public Long removeBySubscriberId(String subscriberId) {
    Subscriber s = getBySubscriberId(subscriberId);
    subscriberRepository.deleteById(s.getId());
    return s.getId();
  }
}
