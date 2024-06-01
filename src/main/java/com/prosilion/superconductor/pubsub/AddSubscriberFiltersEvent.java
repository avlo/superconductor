package com.prosilion.superconductor.pubsub;

import nostr.event.impl.Filters;

import java.util.List;

public record AddSubscriberFiltersEvent(Long subscriberId, List<Filters> filtersList) {
}
