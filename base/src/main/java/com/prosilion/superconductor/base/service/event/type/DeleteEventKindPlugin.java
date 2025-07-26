package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeleteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final DeleteEventPluginIF deleteEventPlugin;

  public DeleteEventKindPlugin(
      @NonNull EventKindPluginIF<Kind> eventKindPlugin,
      @NonNull DeleteEventPluginIF deleteEventPlugin) {
    super(eventKindPlugin);
    this.deleteEventPlugin = deleteEventPlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    log.debug("processing incoming DELETE EVENT: [{}]", event);
    super.processIncomingEvent(event); // NIP-09 req's saving of event itself
    deleteEventPlugin.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}

