package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventPlugin implements EventPluginIF {
  private final CacheServiceIF cacheServiceIF;

  public EventPlugin(@NonNull CacheServiceIF cacheServiceIF) {
    log.debug("class {} adding:", getClass().getSimpleName());
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    log.debug("{} processIncomingEvent() called with event {}", getClass().getSimpleName(), event.createPrettyPrintJson());
    debugLog(event);
    cacheServiceIF.save(event);
  }

  private void debugLog(EventIF event) {
    Stream.of(event)
        .collect(
            Collectors.toMap(
                EventIF::getClass,
                EventIF::asGenericEventRecord))
        .forEach((eventIFClass, genericEventRecord) ->
            log.debug("Class Type:\n  {}\nSerialized Event Content:\n  {}",
                eventIFClass.getSimpleName(),
                EventIF.createPrettyPrintJson));
  }
}
