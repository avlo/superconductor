package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeleteEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheServiceIF cacheServiceIF;

  public DeleteEventKindPlugin(
      @NonNull EventPluginIF eventPluginIF,
      @NonNull CacheServiceIF cacheServiceIF) {
    super(eventPluginIF);
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    log.debug("processing incoming DELETE EVENT:\n  {}", event.createPrettyPrintJson());
    GenericEventRecord genericEventRecord = super.processIncomingEvent(event);// NIP-09 req's saving of event itself
    cacheServiceIF.deleteEvent(event);
    return genericEventRecord;
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}
