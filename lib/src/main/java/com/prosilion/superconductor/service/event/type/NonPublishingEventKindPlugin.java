package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// DECORATOR 
public class NonPublishingEventKindPlugin extends EventKindPlugin {
  
  public NonPublishingEventKindPlugin(@NonNull EventKindPluginIF<Kind> eventKindPlugin) {
    super(eventKindPlugin.getKind(), eventKindPlugin);
  }

  
  @Override
  public void processIncomingEvent(GenericEventKindIF event) {
//    TODO: as per below debug comment  
    log.debug("publishing should not occur.  confirm correct then remove this method");
    super.processIncomingEvent(event);
  }
}
