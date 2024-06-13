package com.prosilion.superconductor.pubsub;

import lombok.NonNull;

public record RemoveSubscriberFilterEvent(@NonNull Long subscriberId) {
}
