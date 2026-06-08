package com.prosilion.superconductor.base.service.request.pubsub;

import lombok.NonNull;

public record EoseNotice(@NonNull Long subscriptionHash, @NonNull String subscriberId) {
}
