package com.prosilion.nostrrelay.pubsub;

import nostr.event.list.FiltersList;

public record AddSubscriberFiltersEvent(Long subscriberId, FiltersList filtersList) {
}
