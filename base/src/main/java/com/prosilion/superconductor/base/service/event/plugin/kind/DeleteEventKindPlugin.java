package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.DeleteEventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeleteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final DeleteEventPluginIF deleteEventPlugin;
  private final NonMaterializedEventKindPlugin nonMaterializedEventKindPlugin;

  public DeleteEventKindPlugin(
      @NonNull NonMaterializedEventKindPlugin nonMaterializedEventKindPlugin,
      @NonNull DeleteEventPluginIF deleteEventPlugin) {
    super(nonMaterializedEventKindPlugin);
    this.deleteEventPlugin = deleteEventPlugin;
    this.nonMaterializedEventKindPlugin = nonMaterializedEventKindPlugin;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    log.debug("processing incoming DELETE EVENT: [{}]", event);
    super.processIncomingEvent(event); // NIP-09 req's saving of event itself
    deleteEventPlugin.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return super.getKind();
  }

  @Override
  public BaseEvent materialize(EventIF eventIF) {
    return nonMaterializedEventKindPlugin.materialize(eventIF);
  }
}
