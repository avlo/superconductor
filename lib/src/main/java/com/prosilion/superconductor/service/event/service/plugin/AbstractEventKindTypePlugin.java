package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.type.AbstractEventKindPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractEventKindTypePlugin implements EventKindTypePluginIF {
  private final AbstractEventKindPlugin abstractEventKindPlugin;

  @Autowired
  public AbstractEventKindTypePlugin(AbstractEventKindPlugin abstractEventKindPlugin) {
    this.abstractEventKindPlugin = abstractEventKindPlugin;
  }

  @Override
  public void processIncomingEvent(GenericEventKindTypeIF event) {
    abstractEventKindPlugin.processIncomingEvent(event);
  }

  public void save(@NonNull GenericEventKindIF event) {
    abstractEventKindPlugin.save(event);
  }

  public AbstractEventKindPlugin getAbstractEventKindPlugin() {
    return abstractEventKindPlugin;
  }
}
