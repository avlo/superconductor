package com.prosilion.superconductor.autoconfigure.base;

import lombok.NonNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class EventKindsNoAuthCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
    return context
        .getEnvironment()
        .getProperty("superconductor.auth.event.kinds", String.class, "")
        .isEmpty();
  }
}
