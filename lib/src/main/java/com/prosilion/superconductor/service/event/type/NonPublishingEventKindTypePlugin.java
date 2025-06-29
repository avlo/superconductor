package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class NonPublishingEventKindTypePlugin extends EventKindTypePlugin {

  public NonPublishingEventKindTypePlugin(@NonNull EventKindTypePluginIF<KindTypeIF> eventKindTypePlugin) {
    super(eventKindTypePlugin.getKindType(), eventKindTypePlugin);
  }

  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
//    TODO: as per below debug comment    
    log.debug("publishing should not occur.  confirm correct then remove this method");
    super.processIncomingEvent(event);
  }
}
