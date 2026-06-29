//package com.prosilion.superconductor.autoconfigure.base.service.event.award;
//
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
//import com.prosilion.nostr.event.BadgeDefinitionGenericEventAux;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
//import java.util.Optional;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public abstract class CacheBadgeAwardAbstractEventAuxService<S extends BadgeDefinitionGenericEventAux, T extends BadgeAwardGenericEventAux> {
//  private static final String EMPTY_OPTIONAL = "[EMPTY OPTIONAL]";
//  protected final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;
//
//  public CacheBadgeAwardAbstractEventAuxService(@NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF) {
//    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
//  }
//
//  public Optional<T> getEvent(@NonNull String eventId, @NonNull String url) {
//    log.debug("... calling cacheReferenceEventTagServiceIF.getEvent(eventId, url): [{}], [{}]", eventId, url);
//
//    Optional<GenericEventRecord> unpopulatedEvent = cacheReferenceEventTagServiceIF.getEvent(eventId, url);
//    log.debug("returned pre-materialized optGER:\n{}",
//       unpopulatedEvent.map(GenericEventRecord::createPrettyPrintJson).orElse(EMPTY_OPTIONAL));
//
//    log.debug("... calling unpopulatedEvent.map(this::materialize ...");
//    Optional<T> t = unpopulatedEvent.flatMap(this::materialize);
//    log.debug("... returned materialized event type [{}]:\n{}",
//       t.map(T::getClass).map(Class::getSimpleName),
//       t.map(EventIF::createPrettyPrintJson).orElse(EMPTY_OPTIONAL));
//
//    return t;
//  }
//
//  protected abstract Optional<T> materialize(@NonNull EventIF eventIF, Relay relay);
//}
