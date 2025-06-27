package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.service.plugin.AbstractEventKindTypePlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractPublishingEventKindTypePlugin<T extends Kind, U extends KindTypeIF> extends AbstractEventKindTypePlugin<T, U> {

  @Autowired
  public AbstractPublishingEventKindTypePlugin(AbstractPublishingEventKindPlugin<T> abstractPublishingEventKindPlugin) {
    super(abstractPublishingEventKindPlugin);
  }

  public void processIncomingEvent(@NonNull GenericEventKindTypeIF event) {
    super.processIncomingEvent(event);
  }

  abstract public void processIncomingPublishingEventKindType(@NonNull GenericEventKindTypeIF event);
}
