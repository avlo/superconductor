package com.prosilion.superconductor.base.service.event.auth;

import java.util.Arrays;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AuthEventKindsCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String kinds = context
        .getEnvironment()
        .getProperty("superconductor.auth.event.kinds", String.class, "");

    if (kinds.isEmpty()) {
      return false;
    }

    return !Arrays.stream(kinds.split(",")).toList().isEmpty();
  }
}
