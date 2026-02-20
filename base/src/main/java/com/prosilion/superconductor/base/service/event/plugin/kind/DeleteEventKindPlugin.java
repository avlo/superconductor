package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeleteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheServiceIF cacheServiceIF;

  public DeleteEventKindPlugin(
      @NonNull EventPlugin eventPlugin,
      @NonNull CacheServiceIF cacheServiceIF) {
    super(eventPlugin);
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    log.debug("processing incoming DELETE EVENT:\n  {}", event.createPrettyPrintJson());
    super.processIncomingEvent(event);// NIP-09 req's saving of event itself
    cacheServiceIF.deleteEvent(event);
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}
