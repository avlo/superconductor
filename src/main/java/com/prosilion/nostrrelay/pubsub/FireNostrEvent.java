package com.prosilion.nostrrelay.pubsub;

import nostr.event.impl.TextNoteEvent;

public record FireNostrEvent(Long subscriberId, TextNoteEvent event) {
}
