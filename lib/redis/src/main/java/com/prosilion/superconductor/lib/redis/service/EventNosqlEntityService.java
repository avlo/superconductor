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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Example;
import org.springframework.lang.NonNull;

@Slf4j
public class EventNosqlEntityService implements EntityServiceIF<EventNosqlEntityIF, EventNosqlEntityIF> {
  private final EventNosqlEntityRepository eventNosqlEntityRepository;
  private final EventNosqlEntityByExampleRepository eventNosqlEntityByExampleRepository;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public EventNosqlEntityService(
      @NonNull EventNosqlEntityRepository eventNosqlEntityRepository,
      @NonNull EventNosqlEntityByExampleRepository eventNosqlEntityByExampleRepository,
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    this.eventNosqlEntityRepository = eventNosqlEntityRepository;
    this.eventNosqlEntityByExampleRepository = eventNosqlEntityByExampleRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptor::getCode,
            Function.identity()));
    log.debug("Created EventNosqlEntityService with interceptors:\n");
    interceptors.stream().map(TagInterceptor::toString).map(s -> Strings.concat("  ", s)).forEach(log::debug);
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
    return eventNosqlEntityRepository.save(convertDtoToNosqlEntity(eventNosqlEntity));
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
            eventNosqlEntityIF.getTags().stream()
                .filter(PubKeyTag.class::isInstance)
                .map(PubKeyTag.class::cast)
                .map(PubKeyTag::getPublicKey)
                .collect(Collectors.toSet()).contains(referencedPublicKey))
        .toList();
  }

  //  TODO: replace with JPQL
  public List<EventNosqlEntityIF> getEventsByKindAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull IdentifierTag identifierTag) {
    EventNosqlEntity eventNosqlEntity = new EventNosqlEntity();
    eventNosqlEntity.setKind(kind.getValue());
    return eventNosqlEntityByExampleRepository.findAll(Example.of(eventNosqlEntity)).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(IdentifierTag.class::isInstance)
                .map(IdentifierTag.class::cast)
                .anyMatch(identifierTag::equals)).toList();
  }

  public Optional<EventNosqlEntityIF> getEventsByKindAndAddressTag(Kind kind, AddressTag addressTag) {
    EventNosqlEntity eventNosqlEntity = new EventNosqlEntity();
    eventNosqlEntity.setKind(kind.getValue());
    eventNosqlEntity.setPublicKey(addressTag.getPublicKey().toHexString());
    Stream<EventNosqlEntityIF> eventNosqlEntityIFStream = eventNosqlEntityByExampleRepository.findAll(Example.of(eventNosqlEntity)).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(IdentifierTag.class::isInstance)
                .map(IdentifierTag.class::cast)
                .anyMatch(addressTag.getIdentifierTag()::equals));
    return eventNosqlEntityIFStream.findFirst();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PubKeyTag referencedPublicKey,
      @NonNull AddressTag addressTag) {
    EventNosqlEntity eventNosqlEntity = new EventNosqlEntity();
    eventNosqlEntity.setKind(kind.getValue());
    eventNosqlEntity.setTags(List.of(referencedPublicKey, addressTag));
    Stream<EventNosqlEntityIF> eventNosqlEntityIFStream = eventNosqlEntityByExampleRepository.findAll(Example.of(eventNosqlEntity)).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(AddressTag.class::isInstance)
                .map(AddressTag.class::cast)
                .anyMatch(addressTag::equals));
    return eventNosqlEntityIFStream.toList();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PubKeyTag referencedPublicKey,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndIdentifierTag = getEventsByKindAndIdentifierTag(kind, identifierTag);
    List<EventNosqlEntityIF> list = eventsByKindAndIdentifierTag.stream()
        .filter(eventNosqlEntityIF ->
            containsTypedTargetTag(referencedPublicKey, eventNosqlEntityIF.getTags()))
        .toList();
    return list;
  }

  //  TODO: replace with JPQL
  public Optional<EventNosqlEntityIF> getEventByKindAndAuthorPublicKeyAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey authorPublicKey,
      @NonNull IdentifierTag identifierTag) {
    EventNosqlEntity eventNosqlEntity = new EventNosqlEntity();
    eventNosqlEntity.setKind(kind.getValue());
    eventNosqlEntity.setPublicKey(authorPublicKey.toHexString());
    return eventNosqlEntityByExampleRepository.findAll(Example.of(eventNosqlEntity)).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(IdentifierTag.class::isInstance)
                .map(IdentifierTag.class::cast)
                .anyMatch(identifierTag::equals)).findFirst();
  }

  private EventNosqlEntity convertDtoToNosqlEntity(EventIF dto) {
    return processInterceptors(dto);
  }

  //  TODO: functionalize below two methods into one

  private EventNosqlEntity processInterceptors(EventIF eventIF) {
    EventNosqlEntity entity = EventNosqlEntity.of(
        eventIF.getId(),
        eventIF.getKind().getValue(),
        eventIF.getPublicKey().toString(),
        eventIF.getCreatedAt(),
        eventIF.getContent(),
        eventIF.getSignature().toString());

    log.debug("processInterceptors(...), pre intercept...");
    entity.setTags(
        Stream.concat(
                eventIF.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            (BaseTag) interceptor.intercept(baseTag)).toList()).flatMap(Collection::stream),
                eventIF.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .toList());
    log.debug("processInterceptors(...), post intercept, done");

    return entity;
  }

  protected EventNosqlEntityIF revertInterceptor(EventNosqlEntityIF eventIf) {
    EventNosqlEntity entity = EventNosqlEntity.of(
        eventIf.getId(),
        eventIf.getKind().getValue(),
        eventIf.getPublicKey().toString(),
        eventIf.getCreatedAt(),
        eventIf.getContent(),
        eventIf.getSignature().toString());

    log.debug("revertInterceptor(...), pre canonicalize...");
    entity.setTags(
        Stream.concat(
                eventIf.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            interceptor.canonicalize((RedisBaseTagIF) baseTag)).toList()).flatMap(Collection::stream),
                eventIf.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .toList());

    log.debug("revertInterceptor(...), post canonicalize, done");
    return entity;
  }

  private <T extends BaseTag> boolean containsTypedTargetTag(T targetTagType, List<BaseTag> baseTags) {
    log.debug("containsTypedTargetTag(T targetTagType, List<BaseTag> baseTags)...");
    log.debug("targetTagType: {}", targetTagType);
    boolean contains = baseTags.stream()
        .filter(targetTagType.getClass()::isInstance)
        .map(targetTagType.getClass()::cast)
        .collect(Collectors.toSet()).contains(targetTagType);
    log.debug("containsTypedTargetTag(...) done");
    return contains;
  }
}
