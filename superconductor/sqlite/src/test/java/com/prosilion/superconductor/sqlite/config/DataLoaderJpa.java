package com.prosilion.superconductor.sqlite.config;

import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

public class DataLoaderJpa implements DataLoaderJpaIF {
  private final EventPlugin eventPlugin;
  private final BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent;
  private final BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent;

  public DataLoaderJpa(@NonNull EventPlugin eventPlugin, @NonNull @Qualifier("badgeDefinitionUpvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionUpvoteEvent, @NonNull @Qualifier("badgeDefinitionDownvoteEvent") BadgeDefinitionGenericEvent badgeDefinitionDownvoteEvent) {
    this.eventPlugin = eventPlugin;
    this.badgeDefinitionUpvoteEvent = badgeDefinitionUpvoteEvent;
    this.badgeDefinitionDownvoteEvent = badgeDefinitionDownvoteEvent;
  }

  @Override
  public void run(String... args) {
//    eventPlugin.processIncomingEvent(
//        new GenericEventKindDto(badgeDefinitionUpvoteEvent).convertBaseEventToEventIF());
//    eventPlugin.processIncomingEvent(
//        new GenericEventKindDto(badgeDefinitionDownvoteEvent).convertBaseEventToEventIF());
  }
}
