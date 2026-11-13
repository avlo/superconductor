package com.prosilion.superconductor.autoconfigure.base.condition;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventCurationActiveCondition implements Condition {
  @Override
  public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
    boolean match = context
       .getEnvironment()
       .getProperty("superconductor.event.curation.active", Boolean.class, false);
    log.info("EventCurationActiveCondition matches? [{}]", match);
    return match;
  }
}
