package com.prosilion.superconductor.service.request.pubsub;

import org.springframework.lang.NonNull;

public record RemoveSubscriberFilter(@NonNull Long subscriberId) {
}
