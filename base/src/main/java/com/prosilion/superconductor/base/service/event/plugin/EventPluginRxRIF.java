//package com.prosilion.superconductor.base.service.event.plugin;
//
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.superconductor.base.cache.CacheServiceIF;
//import java.util.Optional;
//import lombok.NonNull;
//
//public interface EventPluginRxRIF<T> {
//  Optional<T> processIncomingEvent(@NonNull T event, @NonNull Relay fromRelay);
//
//  default <U extends BaseEvent> Optional<GenericEventRecord> eventAlreadyExistsMethod(
//     CacheServiceIF cacheServiceRxRIF,
//     U incomingEvent) {
//    return cacheServiceRxRIF.getEventByEventId(incomingEvent.getId());
//  }
//
//}
