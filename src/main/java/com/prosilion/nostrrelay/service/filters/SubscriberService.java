package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.repository.SubscriberRepository;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriberService {
  private final SubscriberRepository subscriberRepository;

  public SubscriberService() {
    subscriberRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberRepository.class);
  }

  public Subscriber save(Subscriber subscriber) {
    return Optional.of(subscriberRepository.save(subscriber)).orElseThrow(NoResultException::new);
  }
}
