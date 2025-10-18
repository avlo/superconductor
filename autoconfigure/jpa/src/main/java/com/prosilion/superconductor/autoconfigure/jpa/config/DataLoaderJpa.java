package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.nostr.event.BadgeAwardDefinitionEvent;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

public class DataLoaderJpa implements DataLoaderJpaIF {
  private final EventPluginIF eventPlugin;
  private final BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent;
  private final BadgeAwardDefinitionEvent downvoteBadgeDefinitionEvent;

  public DataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("upvoteBadgeDefinitionEvent") BadgeAwardDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull @Qualifier("downvoteBadgeDefinitionEvent") BadgeAwardDefinitionEvent downvoteBadgeDefinitionEvent) {
    this.eventPlugin = eventPlugin;
    this.upvoteBadgeDefinitionEvent = upvoteBadgeDefinitionEvent;
    this.downvoteBadgeDefinitionEvent = downvoteBadgeDefinitionEvent;
  }

  @Override
  public void run(String... args) {
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(upvoteBadgeDefinitionEvent).convertBaseEventToEventIF());
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(downvoteBadgeDefinitionEvent).convertBaseEventToEventIF());
  }
}
