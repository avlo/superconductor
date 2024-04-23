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

	public Subscriber save(Subscriber subscriber) {
		return Optional.of(subscriberRepository.save(subscriber)).orElseThrow(NoResultException::new);
	}

	public Optional<Subscriber> get(Long subscriberId) {
		return Optional.of(subscriberRepository.findById(subscriberId)).orElseThrow(NoResultException::new);
	}
}
