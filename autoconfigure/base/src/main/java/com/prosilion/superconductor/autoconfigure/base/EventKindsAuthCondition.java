package com.prosilion.superconductor.autoconfigure.base;

import java.util.Arrays;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

@Component
public class EventKindsAuthCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    String kinds = context
        .getEnvironment()
        .getProperty("superconductor.auth.event.kinds", String.class, "");

    if (kinds.isEmpty()) {
      return false;
    }

    boolean b = !Arrays.stream(kinds.split(",")).toList().isEmpty();
    return b;
  }
}
