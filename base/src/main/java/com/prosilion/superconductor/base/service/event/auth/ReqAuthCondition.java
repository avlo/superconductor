package com.prosilion.superconductor.base.service.event.auth;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ReqAuthCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    return context
        .getEnvironment()
        .getProperty("superconductor.auth.req.active", Boolean.class, false);
  }
}
