package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.repository.SubscriberRepository;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriberManager {
  private final SubscriberRepository subscriberRepository;

  @Autowired
  public SubscriberManager() {
    this.subscriberRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberRepository.class);
  }

  public Subscriber save(Subscriber subscriber) {
    return Optional.of(subscriberRepository.save(subscriber)).orElseThrow(NoResultException::new);
  }
}
