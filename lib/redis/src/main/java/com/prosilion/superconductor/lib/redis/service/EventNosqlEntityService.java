package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.EntityServiceIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.interceptor.RedisBaseTagIF;
import com.prosilion.superconductor.lib.redis.interceptor.TagInterceptor;
import com.prosilion.superconductor.lib.redis.repository.EventNosqlEntityByExampleRepository;
import com.prosilion.superconductor.lib.redis.repository.EventNosqlEntityRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventNosqlEntityService implements EntityServiceIF<EventNosqlEntityIF, EventNosqlEntityIF> {
  private final EventNosqlEntityRepository eventNosqlEntityRepository;
  //  private final EventNosqlEntityByExampleRepository eventNosqlEntityByExampleRepository;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public EventNosqlEntityService(
     @NonNull EventNosqlEntityRepository eventNosqlEntityRepository,
     @NonNull EventNosqlEntityByExampleRepository eventNosqlEntityByExampleRepository, // TODO: impl later 
     @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    this.eventNosqlEntityRepository = eventNosqlEntityRepository;
//    this.eventNosqlEntityByExampleRepository = eventNosqlEntityByExampleRepository;
    this.interceptors = interceptors.stream().collect(
       Collectors.toMap(
          TagInterceptor::getCode,
          Function.identity()));
    log.debug("Created EventNosqlEntityService with interceptors:\n  {}", 
       interceptors.stream().map(TagInterceptor::toString).collect(Collectors.joining(",\n  ")));
  }

  //  TODO: consider an economically efficient alternative
  @Override
  public List<EventNosqlEntityIF> getAll() {
    return eventNosqlEntityRepository.findAllCustom().stream()
       .map(this::revertInterceptor)
       .toList();
  }

  @Override
  public EventNosqlEntityIF save(@NonNull EventIF eventNosqlEntity) {
    return revertInterceptor(
       eventNosqlEntityRepository.save(
          processInterceptor(eventNosqlEntity)));
  }

  @Override
  public Optional<EventNosqlEntityIF> findByEventIdString(@NonNull String eventId) {
    return eventNosqlEntityRepository.findByEventId(eventId).map(this::revertInterceptor);
  }

  //  TODO: replace with JPQL
  @Override
  public List<EventNosqlEntityIF> getEventsByKind(@NonNull Kind kind) {
    return eventNosqlEntityRepository.findByKind(kind).stream()
       .map(this::revertInterceptor)
       .toList();
  }

  @Override
  public List<EventNosqlEntityIF> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return eventNosqlEntityRepository.findByKindAndAuthorPublicKey(kind, authorPublicKey).stream()
       .map(this::revertInterceptor)
       .toList();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag referencedPublicKey) {
    return getEventsByKind(kind).stream()
       .filter(eventNosqlEntityIF ->
          containsTypedTargetTag(referencedPublicKey, eventNosqlEntityIF)).toList();
  }

  //  TODO: replace with JPQL
  public List<EventNosqlEntityIF> getEventsByKindAndIdentifierTag(
     @NonNull Kind kind,
     @NonNull IdentifierTag identifierTag) {
    return getEventsByKind(kind).stream()
       .filter(eventNosqlEntityIF ->
          containsTypedTargetTag(identifierTag, eventNosqlEntityIF)).toList();
  }

  public List<EventNosqlEntityIF> getEventsByKindAndAddressTag(
     @NonNull Kind kind,
     @NonNull AddressTag addressTag) {
    return getEventsByKind(kind).stream()
       .filter(eventNosqlEntityIF ->
          containsTypedTargetTag(addressTag, eventNosqlEntityIF)).toList();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag referencedPublicKey,
     @NonNull AddressTag addressTag) {
    return getEventsByKindAndPubKeyTag(kind, referencedPublicKey).stream()
       .filter(eventNosqlEntityIF ->
          containsTypedTargetTag(addressTag, eventNosqlEntityIF)).toList();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndIdentifierTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag referencedPublicKey,
     @NonNull IdentifierTag identifierTag) {
    return getEventsByKindAndPubKeyTag(kind, referencedPublicKey).stream()
       .filter(eventNosqlEntityIF ->
          containsTypedTargetTag(identifierTag, eventNosqlEntityIF)).toList();
  }

  //  TODO: replace with JPQL
  public Optional<EventNosqlEntityIF> getEventByKindAndAuthorPublicKeyAndIdentifierTag(
     @NonNull Kind kind,
     @NonNull PublicKey authorPublicKey,
     @NonNull IdentifierTag identifierTag) {
    return getEventsByKindAndAuthorPublicKey(kind, authorPublicKey).stream()
       .filter(eventNosqlEntityIF ->
          containsTypedTargetTag(identifierTag, eventNosqlEntityIF)).findFirst();
  }

  private EventNosqlEntity processInterceptor(EventIF eventIF) {
    return intercept(eventIF,
       (interceptor, baseTag) -> (BaseTag) interceptor.intercept(baseTag),
       "  intercepting");
  }

  private EventNosqlEntityIF revertInterceptor(EventIF eventIF) {
    return intercept(eventIF,
       (interceptor, baseTag) -> interceptor.canonicalize((RedisBaseTagIF) baseTag),
       "canonicalizing");
  }

  private EventNosqlEntity intercept(EventIF eventIf, BiFunction<TagInterceptor<BaseTag, RedisBaseTagIF>, BaseTag, BaseTag> fxn, String mode) {
    EventNosqlEntity entity = EventNosqlEntity.of(
       eventIf.getId(),
       eventIf.getKind().getValue(),
       eventIf.getPublicKey().toString(),
       eventIf.getCreatedAt(),
       eventIf.getContent(),
       eventIf.getSignature().toString());

    entity.setTags(
       Stream.concat(
             eventIf.getTags().stream().map(baseTag ->
                   Optional.ofNullable(
                         interceptors.get(baseTag.getCode()))
                      .stream()
                      .map(interceptor ->
                         fxn.apply(interceptor, baseTag)).toList())
                .flatMap(Collection::stream),
             eventIf.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
          .toList());

    log.debug("{}... [{}] ... done", mode, entity.getTags().stream()
       .map(BaseTag::getClass)
       .map(Class::getSimpleName).sorted()
       .collect(Collectors.joining(", ")));
    return entity;
  }

  private <T extends BaseTag> boolean containsTypedTargetTag(final T targetTagType, final EventIF eventIF) {
//    final String simpleName = targetTagType.getClass().getSimpleName();
//    final List<String> sorted = eventIF.getTags().stream().map(Object::getClass).map(Class::getSimpleName).sorted().toList();
//    final String join = String.join("], [", sorted);
//    boolean empty = sorted.contains(simpleName);
//    log.debug("baseTags contains typedTarget?  [{}]", empty);
//    log.debug("typedTarget:\n  [{}]\n  baseTags:\n  [{}]", simpleName, join);
//    return empty;
//    log.debug("containsTypedTargetTag(T targetTagType, List<BaseTag> baseTags)...");
//    log.debug("targetTagType: {}", targetTagType);

//    TODO: below line investigate why doesn't work (caused BaseCacheServiceIT failure)
//      potential relation to <T extends BaseTag> param
//    return eventIF.getTypeSpecificTags(targetTagType.getClass()).isEmpty();

    return eventIF.getTags().stream()
       .filter(targetTagType.getClass()::isInstance)
       .map(targetTagType.getClass()::cast)
       .collect(Collectors.toSet()).contains(targetTagType);
  }
}
