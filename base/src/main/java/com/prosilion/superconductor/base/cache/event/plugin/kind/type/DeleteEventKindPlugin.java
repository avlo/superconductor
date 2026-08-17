package com.prosilion.superconductor.base.cache.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    log.debug("processing incoming DELETE EVENT:\n  {}", event.createPrettyPrintJson());
    Optional<GenericEventRecord> genericEventRecord = super.processIncomingEvent(event, fromRelay);// NIP-09 req's saving of event itself
    genericEventRecord.ifPresent(cacheServiceIF::deleteEvent);
    return genericEventRecord;
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}
