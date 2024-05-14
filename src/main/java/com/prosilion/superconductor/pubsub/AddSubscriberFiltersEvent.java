package com.prosilion.superconductor.pubsub;

import nostr.event.list.FiltersList;

public record AddSubscriberFiltersEvent(Long subscriberId, FiltersList filtersList) {
}
