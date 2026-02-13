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
      @NonNull PublicKey referencedPublicKey) {
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
    return getEventsByKind(kind).stream()
        .filter(eventNosqlEntityIF ->
            containsIdentifierTag(identifierTag, eventNosqlEntityIF))
        .toList();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencedPublicKey,
      @NonNull AddressTag addressTag) {
    EventNosqlEntity eventNosqlEntity = new EventNosqlEntity();
    eventNosqlEntity.setKind(kind.getValue());
    eventNosqlEntity.setTags(List.of(new PubKeyTag(referencedPublicKey), addressTag));
    return eventNosqlEntityByExampleRepository.findAll(Example.of(eventNosqlEntity)).stream()
        .map(this::revertInterceptor).toList();
  }

  //  TODO: replace with JPQL  
  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencedPublicKey,
      @NonNull IdentifierTag identifierTag) {
    return getEventsByKindAndPubKeyTag(kind, referencedPublicKey).stream()
        .filter(eventNosqlEntityIF ->
            containsIdentifierTag(identifierTag, eventNosqlEntityIF))
        .toList();
  }

  //  TODO: replace with JPQL
  public List<EventNosqlEntityIF> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey authorPublicKey,
      @NonNull IdentifierTag identifierTag) {
    return eventNosqlEntityRepository.findByKindAndAuthorPublicKey(kind, authorPublicKey).stream()
        .filter(eventNosqlEntityIF ->
            containsIdentifierTag(identifierTag, eventNosqlEntityIF))
        .toList();
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

    entity.setTags(
        Stream.concat(
                eventIF.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            (BaseTag) interceptor.intercept(baseTag)).toList()).flatMap(Collection::stream),
                eventIF.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .toList());

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

    entity.setTags(
        Stream.concat(
                eventIf.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            interceptor.canonicalize((RedisBaseTagIF) baseTag)).toList()).flatMap(Collection::stream),
                eventIf.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .toList());

    return entity;
  }

  private static boolean containsIdentifierTag(IdentifierTag identifierTag, EventNosqlEntityIF eventNosqlEntityIF) {
    return eventNosqlEntityIF.getTags().stream()
        .filter(IdentifierTag.class::isInstance)
        .map(IdentifierTag.class::cast)
        .collect(Collectors.toSet()).contains(identifierTag);
  }
}
