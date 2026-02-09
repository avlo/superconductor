//package com.prosilion.superconductor.autoconfigure.base.service.event;
//
//import com.prosilion.nostr.NostrException;
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BadgeAwardGenericEvent;
//import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.FollowSetsEvent;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.filter.Filterable;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.RelayTag;
//import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventServiceIF;
//import com.prosilion.superconductor.base.cache.CacheFollowSetsEventServiceIF;
//import com.prosilion.superconductor.base.cache.CacheServiceIF;
//import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
//import com.prosilion.superconductor.base.service.event.plugin.kind.EventMaterializer;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//
//@Slf4j
//public class CanonicalEventService implements EventMaterializer {
//  private final CacheServiceIF cacheServiceIF;
//  
//  public CanonicalEventService(
//      @NonNull CacheServiceIF cacheServiceIF) {
//    this.cacheServiceIF = cacheServiceIF;
//  }
//
//  public Optional<BaseEvent> getEvent(@NonNull String eventId, @NonNull String url) {
//    return cacheServiceIF.getEventByEventId(eventId);
//  }
//
//  @Override
//  public FollowSetsEvent materialize(@NonNull EventIF incomingFollowSetsEvent) {
//  }
//}
