package com.prosilion.nostrrelay.pubsub;

import com.prosilion.nostrrelay.entity.EventEntity;
import nostr.event.Kind;

public record AddNostrEvent(Kind kind, EventEntity eventEntity) {
}
