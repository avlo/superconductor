package com.prosilion.superconductor.service.event.service.plugin;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.type.AbstractEventKindPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractEventKindTypePlugin<T extends Kind, U extends KindTypeIF> implements EventKindTypePluginIF<T, U> {
  private final AbstractEventKindPlugin<T> abstractEventKindPlugin;

  @Autowired
  public AbstractEventKindTypePlugin(AbstractEventKindPlugin<T> abstractEventKindPlugin) {
    this.abstractEventKindPlugin = abstractEventKindPlugin;
  }

  @Override
  public void processIncomingEvent(GenericEventKindTypeIF event) {
    abstractEventKindPlugin.processIncomingEvent(event);
  }

  public void save(@NonNull GenericEventKindIF event) {
    abstractEventKindPlugin.save(event);
  }

  public AbstractEventKindPlugin<T> getAbstractEventKindPlugin() {
    return abstractEventKindPlugin;
  }
}
