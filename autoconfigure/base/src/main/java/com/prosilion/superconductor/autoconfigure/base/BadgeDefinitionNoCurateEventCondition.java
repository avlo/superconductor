package com.prosilion.superconductor.autoconfigure.base;

import lombok.NonNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class BadgeDefinitionNoCurateEventCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
    return !context
       .getEnvironment()
       .getProperty("superconductor.curated.badgedefinition.event", Boolean.class, true);
  }
}
