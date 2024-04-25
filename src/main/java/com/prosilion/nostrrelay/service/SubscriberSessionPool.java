package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddSubscriberEvent;
import com.prosilion.nostrrelay.pubsub.RemoveSubscriberFilterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;

@Service
public class SubscriberSessionPool {
	private final Set<Long> subscriberIds = new TreeSet<>();

	@EventListener
	public void event(AddSubscriberEvent addSubscriberEvent) {
		subscriberIds.add(addSubscriberEvent.getSubscriberId());
	}

	@EventListener
	public void event(RemoveSubscriberFilterEvent removeSubscriberFilterEvent) {
		subscriberIds.remove(removeSubscriberFilterEvent.subscriberId());
	}
}
