//package com.prosilion.superconductor.autoconfigure.base.service;
//
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.event.TagMappedEventIF;
//import com.prosilion.nostr.filter.Filterable;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.tag.ReferentialEventTag;
//import com.prosilion.nostr.user.NostrCheckedException;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.superconductor.base.service.CacheTagEventServiceIF;
//import com.prosilion.superconductor.base.service.event.CacheServiceIF;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Function;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//
//@Slf4j
//public abstract class CacheAbstractTagEventService implements CacheTagEventServiceIF {
//  public static final String NON_EXISTENT_TAG_ID_S = "Event ID [%s] contains %s(s) tags referencing non-existent mapped event";
//  private final CacheServiceIF cacheServiceIF;
//  private final Class<T> clazz;
//
//  public CacheAbstractTagEventService(
//      CacheServiceIF cacheServiceIF,
//      Class<T> clazz) {
//    this.cacheServiceIF = cacheServiceIF;
//    this.clazz = clazz;
//  }
//
//  @Override
//  public void save(EventIF event) throws NostrCheckedException {
//    log.info("{} saving event: [{}] ...", getClass().getSimpleName(), event.toString());
//    log.info("... with eventId [{}] ...", event.getId());
//    List<? extends ReferentialEventTag> clazzTags = Filterable.getTypeSpecificTags(clazz, event);
//    if (!clazzTags.isEmpty()) {
//      log.info("... with {}(s) tags:", clazz.getSimpleName());
//    }
//
//    clazzTags.forEach(tag -> log.info("event type [{}], event URL [{}]", tag.getClass().getSimpleName(), tag.getRelay().getUrl()));
//
//    List<GenericEventRecord> foundDbEvents = new ArrayList<>();
//    for (T clazzTag : clazzTags) {
//      foundDbEvents.add(getTagEventByType(clazzTag, event));
//    }
//
//    if (!foundDbEvents.isEmpty()) {
//      log.info("... found related DB events:");
//    }
//    foundDbEvents.forEach(ger -> log.info("eventId [{}]", ger.getId()));
//
//    log.info("all required Tag events found, saving event with eventId [{}] ...", event.getId());
//    cacheServiceIF.save(event);
//    log.info("...done");
//  }
//
//  @Override
//  public <S extends BaseEvent> S createEventGivenMappedTag(
//      GenericEventRecord eventToPopulate,
//      Class<S> eventClassFromKind,
//      Function<T, ? extends BaseEvent> fxn) {
//    S eventWithEventTagsMappedEvents = createBaseEvent(
//        eventToPopulate,
//        eventClassFromKind,
//        fxn);
//    return eventWithEventTagsMappedEvents;
//  }
//
//  public <S extends BaseEvent> S createBaseEvent(
//      @NonNull GenericEventRecord genericEventRecord,
//      @NonNull Class<S> baseEventFromKind) {
//    S event = cacheServiceIF.createBaseEvent(
//        genericEventRecord,
//        baseEventFromKind);
//    return event;
//  }
//
//  abstract public TagMappedEventIF populate(GenericEventRecord baseEvent, List<GenericEventRecord> unpopulatedEvents);
//
//  @Override
//  public GenericEventRecord getTagEventByType(@NonNull T addressTag, EventIF event) throws NostrCheckedException {
//
//    GenericEventRecord eventByAddressTag = cacheServiceIF.getEvent(addressTag).orElseThrow(() -> new NostrCheckedException(
//        String.format(NON_EXISTENT_TAG_ID_S, event.getId(), addressTag)));
//
//    return eventByAddressTag;
//  }
//
//  @Override
//  public Optional<TagMappedEventIF> getEvent(@NonNull String eventId) {
//    Optional<GenericEventRecord> optionalFoundRecord = cacheServiceIF.getEventById(eventId);
//    if (optionalFoundRecord.isPresent()) {
//      return optionalFoundRecord.map(genericEventRecord ->
//          populate(genericEventRecord, optionalFoundRecord.map(this::getTagsAsGenericEventRecords).orElseThrow()));
//    }
//    return Optional.empty();
//  }
//
//  @Override
//  public List<TagMappedEventIF> getByKind(@NonNull Kind kind) {
//    List<GenericEventRecord> eventsByKind = cacheServiceIF.getEvents(kind);
//    return eventsByKind.stream().map(genericEventRecord ->
//        populate(genericEventRecord, getTagsAsGenericEventRecords(genericEventRecord))).toList();
//  }
//
//  @Override
//  public Optional<TagMappedEventIF> getEvent(Kind kind, PublicKey publicKey, IdentifierTag identifierTag) {
//    Optional<GenericEventRecord> event = cacheServiceIF.getEvent(kind, publicKey, identifierTag);
//
//    Optional<TagMappedEventIF> list = event.map(genericEventRecord ->
//        populate(genericEventRecord, getTagsAsGenericEventRecords(genericEventRecord)));
//    return list;
//  }
//
//  public List<GenericEventRecord> getTagsAsGenericEventRecords(@NonNull GenericEventRecord genericEventRecord) {
//    List<T> tagStream = genericEventRecord.getTags().stream()
//        .filter(clazz::isInstance)
//        .map(clazz::cast).toList();
//    List<Optional<GenericEventRecord>> optionalStream = tagStream.stream()
//        .map(getEvent()).toList();
//    List<GenericEventRecord> eventTagsAsGenericEventRecords = optionalStream.stream()
//        .flatMap(Optional::stream).toList();
//    return eventTagsAsGenericEventRecords;
//  }
//
//  private Function<T, Optional<GenericEventRecord>> getEvent() {
//    Function<T, Optional<GenericEventRecord>> getEvent = cacheServiceIF::getEvent;
//    return getEvent;
//  }
//
//  @Override
//  public Optional<TagMappedEventIF> getEvent(@NonNull EventIF eventIF) {
//    return getEvent(eventIF.getId());
//  }
//
//  @Override
//  public List<TagMappedEventIF> getAll() {
//    List<GenericEventRecord> all = cacheServiceIF.getAll();
//    return all.stream().map(genericEventRecord -> populate(genericEventRecord, all)).toList();
//  }
//
//  @Override
//  public void deleteEvent(@NonNull EventIF eventIF) {
//    cacheServiceIF.deleteEvent(eventIF);
//  }
//}
