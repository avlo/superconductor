package com.prosilion.superconductor.autoconfigure.base.condition;

import lombok.NonNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

@Component
public class EventCurationActiveCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
    return context
       .getEnvironment()
       .getProperty("superconductor.event.curation.active", Boolean.class, true);
  }
}
