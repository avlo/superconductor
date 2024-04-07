package com.prosilion.nostrrelay.service.filters;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriberServiceImpl {
  private final SubscriberRepository subscriberRepository;

  public SubscriberServiceImpl() {
    subscriberRepository = ApplicationContextProvider.getApplicationContext().getBean(SubscriberRepository.class);
  }

  public void save(Subscriber subscriber) {
    subscriberRepository.save(subscriber);
  }
}
