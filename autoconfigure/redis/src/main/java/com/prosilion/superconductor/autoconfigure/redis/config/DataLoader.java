package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.superconductor.base.service.event.type.EventPluginIF;
import com.prosilion.superconductor.lib.redis.dto.GenericDocumentKindDto;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.NonNull;

public class DataLoader implements CommandLineRunner {
  private final EventPluginIF eventPlugin;
  @Getter
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
  public void run(String... args) {
    eventPlugin.processIncomingEvent(
        new GenericDocumentKindDto(upvoteBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());
    eventPlugin.processIncomingEvent(
        new GenericDocumentKindDto(downvoteBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());
  }
}
