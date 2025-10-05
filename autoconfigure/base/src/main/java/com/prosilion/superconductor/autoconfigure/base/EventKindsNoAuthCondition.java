package com.prosilion.superconductor.autoconfigure.base;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class EventKindsNoAuthCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    boolean empty = context
        .getEnvironment()
        .getProperty("superconductor.auth.event.kinds", String.class, "")
        .isEmpty();
    return empty;
  }
}
