package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddSubscriberEvent;
import com.prosilion.nostrrelay.pubsub.RemoveSubscriberFilterEvent;
import com.prosilion.nostrrelay.service.request.SubscriberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

@Service
public class SubscriberPool {
	private final ApplicationEventPublisher publisher;
	private final SubscriberService subscriberService;
	private final Set<Long> subscriberIds;

	@Autowired
	public SubscriberPool(SubscriberService subscriberService, ApplicationEventPublisher publisher) {
		this.subscriberService = subscriberService;
		this.publisher = publisher;
		this.subscriberIds = new TreeSet<>();
	}

	@EventListener
	public void event(AddSubscriberEvent addSubscriberEvent) {
		subscriberIds.add(addSubscriberEvent.getSubscriberId());
	}

	@EventListener
	public void event(RemoveSubscriberFilterEvent removeSubscriberFilterEvent) {
		if (subscriberIds.remove(removeSubscriberFilterEvent.subscriberId())) {
			publisher.publishEvent(new RemoveSubscriberFilterEvent(removeSubscriberFilterEvent.subscriberId()));
		}
	}
}
