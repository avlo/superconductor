//package com.prosilion.superconductor.lib.redis.service;
//
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.tag.AddressTag;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.IdentifierTag;
//import com.prosilion.nostr.tag.PubKeyTag;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
//import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
//import java.util.List;
//import java.util.Optional;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Stream;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class RedisCacheServiceRxR implements RedisCacheServiceIF {
//  private final EventNosqlEntityService eventNosqlEntityService;
//  private final DeletionEventNoSqlEntityService deletionEventNoSqlEntityService;
//
//  public RedisCacheServiceRxR(
//     @NonNull EventNosqlEntityService eventNosqlEntityService,
//     @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService) {
//    this.eventNosqlEntityService = eventNosqlEntityService;
//    this.deletionEventNoSqlEntityService = deletionEventNoSqlEntityService;
//  }
//
//  @Override
//  public <T extends BaseEvent> GenericEventRecord save(T event) {
////    log.debug("(1of3save) save(EventIF event):\n  {}", event.createPrettyPrintJson());
////    EventNosqlEntityIF save = eventNosqlEntityService.save(event);
////    log.debug("... (2of3save) returned EventNosqlEntityIF toString()...:\n  {}", save);
////    GenericEventRecord genericEventRecord = save.asGenericEventRecord();
////    log.debug("... (3of3save) returned EventNosqlEntityIF asGenericEventRecord()...:\n  {}", genericEventRecord);
//    EventNosqlEntityIF eventNosqlEntityIF = eventNosqlEntityService.save(event);
//    return eventNosqlEntityIF.asGenericEventRecord();
//  }
//
//  private final Function<Optional<EventNosqlEntityIF>, Optional<GenericEventRecord>> filteredGER =
//     eventNosqlEntityIF ->
//        filterDeletionEvent(eventNosqlEntityIF)
//           .map(EventIF::asGenericEventRecord);
//
//  @Override
//  public <T extends BaseEvent> Optional<T> getEventByEventId(@NonNull String eventId) {
//    Optional<EventNosqlEntityIF> eventNosqlEntityIF = eventNosqlEntityService.findByEventIdString(eventId)
//       .filter(filterDeletionEvents());
//    return eventNosqlEntityIF;
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getByKind(@NonNull Kind kind) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//    {
//      Stream<EventNosqlEntityIF> eventNosqlEntityIFStream = filterDeletionEvents(eventNosqlEntityIFS);
//      return eventNosqlEntityIFStream;
//    }).apply(
//       eventNosqlEntityService.getEventsByKind(kind));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//    {
//      Stream<EventNosqlEntityIF> eventNosqlEntityIFStream = filterDeletionEvents(eventNosqlEntityIFS);
//      return eventNosqlEntityIFStream;
//    }).apply(
//       eventNosqlEntityService.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTag(
//     @NonNull Kind kind,
//     @NonNull PubKeyTag publicKey) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//    {
//      Stream<EventNosqlEntityIF> eventNosqlEntityIFStream = filterDeletionEvents(eventNosqlEntityIFS);
//      return eventNosqlEntityIFStream;
//    }).apply(
//       eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndEventTag(
//     @NonNull Kind kind,
//     @NonNull EventTag eventTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//       eventNosqlEntityService.getEventsByKindAndEventTag(kind, eventTag));
//  }
//
//  @Override
//  public Optional<T> getFirstEventByKindAndEventTag(
//     @NonNull Kind kind,
//     @NonNull EventTag eventTag) {
//    return filteredGER.apply(
//       eventNosqlEntityService.getFirstEventByKindAndEventTag(kind, eventTag));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTagAndEventTag(
//     @NonNull Kind kind,
//     @NonNull PubKeyTag referencePubKeyTag,
//     @NonNull EventTag eventTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//       eventNosqlEntityService.getEventsByKindAndPubKeyTagAndEventTag(kind, referencePubKeyTag, eventTag));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndIdentifierTag(
//     @NonNull Kind kind,
//     @NonNull IdentifierTag identifierTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//       eventNosqlEntityService.getEventsByKindAndIdentifierTag(kind, identifierTag));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndAddressTag(
//     @NonNull Kind kind,
//     @NonNull AddressTag addressTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//       eventNosqlEntityService.getEventsByKindAndAddressTag(kind, addressTag)
//          .stream().toList());
//  }
//
//  @Override
//  public <T extends BaseEvent> Optional<T> getFirstEventByKindAndAddressTag(
//     @NonNull Kind kind,
//     @NonNull AddressTag addressTag) {
//    Optional<EventNosqlEntityIF> eventNosqlEntityIF = eventNosqlEntityService.getFirstEventByKindAndAddressTag(kind, addressTag);
//    return filteredGER.apply(
//       eventNosqlEntityIF);
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTagAndAddressTag(
//     @NonNull Kind kind,
//     @NonNull PubKeyTag referencePubKeyTag,
//     @NonNull AddressTag addressTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//       eventNosqlEntityService.getEventsByKindAndPubKeyTagAndAddressTag(
//          kind,
//          referencePubKeyTag,
//          addressTag));
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getEventsByKindAndPubKeyTagAndIdentifierTag(
//     @NonNull Kind kind,
//     @NonNull PubKeyTag referencePubKeyTag,
//     @NonNull IdentifierTag identifierTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//       eventNosqlEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(
//          kind,
//          referencePubKeyTag,
//          identifierTag));
//  }
//
//  @Override
//  public <T extends BaseEvent> Optional<T> getEventByKindAndAuthorPublicKeyAndIdentifierTag(
//     @NonNull Kind kind,
//     @NonNull PublicKey authorPublicKey,
//     @NonNull IdentifierTag identifierTag) {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(
//          eventNosqlEntityService.getEventByKindAndAuthorPublicKeyAndIdentifierTag(
//             kind,
//             authorPublicKey,
//             identifierTag).stream().toList())
//       .stream().findFirst();
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getAll() {
//    return ((Function<List<EventNosqlEntityIF>, List<? extends BaseEvent>>) eventNosqlEntityIFS ->
//       filterDeletionEvents(eventNosqlEntityIFS)).apply(eventNosqlEntityService.getAll());
//  }
//
//  @Override
//  public <T extends BaseEvent> List<T> getAllIncludingDeleted() {
//    List<EventNosqlEntityIF> eventNosqlEntityIFList = eventNosqlEntityService.getAll();
//    return eventNosqlEntityIFList;
//  }
//
//  private Stream<EventNosqlEntityIF> filterDeletionEvents(List<EventNosqlEntityIF> events) {
//    return events.stream().filter(filterDeletionEvents());
//  }
//
//  private Optional<EventNosqlEntityIF> filterDeletionEvent(Optional<EventNosqlEntityIF> event) {
//    return event.filter(filterDeletionEvents());
//  }
//
//  private Predicate<EventNosqlEntityIF> filterDeletionEvents() {
//    return eventNosqlEntityIF ->
//       !getAllDeletionEventIds().contains(eventNosqlEntityIF.getEventId());
//  }
//
//  @Override
//  public <T extends BaseEvent> void deleteEvent(@NonNull T eventIF) {
//    deleteEventTags(eventIF, deletionEventNoSqlEntityService::addDeletionEvent);
//  }
//
//  @Override
//  public List<String> getAllDeletionEventIds() {
//    return deletionEventNoSqlEntityService.getAll().stream().map(DeletionEventNosqlEntityIF::getEventId).toList();
//  }
//
//  private void deleteEventTags(
//     @NonNull EventIF event,
//     @NonNull Consumer<EventNosqlEntityIF> addDeletionEvent) {
//    event.getTypeSpecificTags(EventTag.class).stream()
//       .map(EventTag::getEventId)
//       .map(eventNosqlEntityService::findByEventIdString)
//       .flatMap(Optional::stream)
//       .filter(deletionCandidate ->
//          deletionCandidate.getPublicKey().equals(event.getPublicKey()))
//       .forEach(addDeletionEvent);
//  }
//}
