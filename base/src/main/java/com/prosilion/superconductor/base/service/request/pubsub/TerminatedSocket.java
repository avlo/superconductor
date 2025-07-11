package com.prosilion.superconductor.base.service.request.pubsub;

import org.springframework.lang.NonNull;

public record TerminatedSocket(@NonNull String sessionId) {
}
