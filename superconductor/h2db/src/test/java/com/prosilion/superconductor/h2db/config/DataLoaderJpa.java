package com.prosilion.superconductor.h2db.config;

import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

public class DataLoaderJpa implements DataLoaderJpaIF {
  private final EventPluginIF eventPlugin;
  private final BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent;

  public DataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionAwardEvent badgeDefinitionDownvoteEvent) {
    this.eventPlugin = eventPlugin;
    this.badgeDefinitionUpvoteEvent = badgeDefinitionUpvoteEvent;
    this.badgeDefinitionDownvoteEvent = badgeDefinitionDownvoteEvent;
  }

  @Override
  public void run(String... args) {
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(badgeDefinitionUpvoteEvent).convertBaseEventToEventIF());
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(badgeDefinitionDownvoteEvent).convertBaseEventToEventIF());
  }
}
