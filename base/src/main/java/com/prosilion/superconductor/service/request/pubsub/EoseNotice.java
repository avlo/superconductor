package com.prosilion.superconductor.service.request.pubsub;

import org.springframework.lang.NonNull;

public record EoseNotice(@NonNull Long subscriptionHash, @NonNull String subscriberId) {
}
