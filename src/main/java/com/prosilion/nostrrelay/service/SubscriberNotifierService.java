package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.FireNostrEvent;
import com.prosilion.nostrrelay.pubsub.SubscriberNotifierEvent;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SubscriberNotifierService<T extends GenericEvent> {
	private final ApplicationEventPublisher publisher;

	public SubscriberNotifierService(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}

	@EventListener
	public void newEventHandler(SubscriberNotifierEvent<T> subscriberNotifierEvent) {
		Map<Long, FiltersList> subscribersFiltersMap = subscriberNotifierEvent.subscribersFiltersMap();
		AddNostrEvent<T> addNostrEvent = subscriberNotifierEvent.addNostrEvent();

		// iterate subscribers map
		// get the subscribers filters
		// for each subscribers filters, iterate over each filter type
		// for each filter type, see if it matches the relevant event attribute
		// if there's a match, send event to subscriber

		// TODO: prudent replace all below parallelizable
		Map<Long, AddNostrEvent<T>> eventsToSend = new HashMap<>();
		subscribersFiltersMap.forEach((subscriberId, subscriberIdFiltersList) -> {
			subscriberIdFiltersList.getList().forEach(subscriberFilters ->
					addMatch(subscriberFilters, addNostrEvent).ifPresent(event ->
							eventsToSend.put(subscriberId, event)));
		});
		eventsToSend.forEach((subscriberId, event) ->
				publisher.publishEvent(new FireNostrEvent<T>(subscriberId, event.getEvent())));
	}

	private Optional<AddNostrEvent<T>> addMatch(Filters subscriberFilters, AddNostrEvent<T> eventToCheck) {
		// TODO: convert to stream
		for (GenericEvent subscriberEvent : subscriberFilters.getEvents().getList()) {
			String eventToCheckContent = eventToCheck.getEvent().getContent();
			String subscriberInterestContent = subscriberEvent.getId();
			if (subscriberInterestContent.equals(eventToCheckContent)) {
				Optional<AddNostrEvent<T>> optional = Optional.of(eventToCheck);
				return optional;
			}
		}
		return Optional.empty();
	}
}
