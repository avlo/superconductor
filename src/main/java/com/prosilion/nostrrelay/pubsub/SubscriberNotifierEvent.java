package com.prosilion.nostrrelay.pubsub;

import nostr.event.impl.GenericEvent;
import nostr.event.list.FiltersList;

import java.util.Map;

public record SubscriberNotifierEvent<T extends GenericEvent>(Map<Long, FiltersList> subscribersFiltersMap, AddNostrEvent<T> addNostrEvent) {}
