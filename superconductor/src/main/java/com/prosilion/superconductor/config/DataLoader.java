package com.prosilion.superconductor.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.superconductor.dto.GenericEventKindDto;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
  private final EventPluginIF eventPlugin;
  private final BadgeDefinitionEvent upvoteBadgeDefinitionEvent;
  private final BadgeDefinitionEvent downvoteBadgeDefinitionEvent;

  public DataLoader(
      @NonNull @Qualifier("eventPlugin") EventPluginIF eventPlugin,
      @NonNull @Qualifier("upvoteBadgeDefinitionEvent") BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull @Qualifier("downvoteBadgeDefinitionEvent") BadgeDefinitionEvent downvoteBadgeDefinitionEvent) {
    this.eventPlugin = eventPlugin;
    this.upvoteBadgeDefinitionEvent = upvoteBadgeDefinitionEvent;
    this.downvoteBadgeDefinitionEvent = downvoteBadgeDefinitionEvent;
  }

  @Override
  public void run(String... args) throws Exception {
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(upvoteBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(downvoteBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());
  }
}
