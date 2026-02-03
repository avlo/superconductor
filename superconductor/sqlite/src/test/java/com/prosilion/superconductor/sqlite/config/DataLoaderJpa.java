package com.prosilion.superconductor.sqlite.config;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

public class DataLoaderJpa implements DataLoaderJpaIF {
  private final EventPluginIF eventPlugin;
  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent;

  public DataLoaderJpa(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent,
      @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent) {
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
