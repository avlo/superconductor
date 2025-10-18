package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.interceptor.RedisBaseTagIF;
import com.prosilion.superconductor.lib.redis.interceptor.TagInterceptor;
import com.prosilion.superconductor.lib.redis.repository.EventNosqlEntityRepository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventNosqlEntityService {
  private final EventNosqlEntityRepository eventNosqlEntityRepository;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public EventNosqlEntityService(
      @NonNull EventNosqlEntityRepository eventNosqlEntityRepository,
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    this.eventNosqlEntityRepository = eventNosqlEntityRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptor::getCode,
            Function.identity()));
    log.debug("Created EventNosqlEntityService with interceptors:\n");
    interceptors.forEach(interceptor -> log.debug("  {}\n", interceptor));
  }

  public Optional<EventNosqlEntityIF> findByEventIdString(@NonNull String eventId) {
    return eventNosqlEntityRepository.findByEventId(eventId)
        .map(this::revertInterceptor);
  }

  public List<EventNosqlEntityIF> getEventsByKind(@NonNull Kind kind) {
    return eventNosqlEntityRepository.findByKind(kind.getValue()).stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    return eventNosqlEntityRepository.findByKind(
            kind.getValue()).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(PubKeyTag.class::isInstance)
                .map(PubKeyTag.class::cast)
                .map(PubKeyTag::getPublicKey)
                .collect(Collectors.toSet()).contains(publicKey))
        .toList();
  }

  public List<EventNosqlEntityIF> getEventsByKindAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull IdentifierTag identifierTag) {
    return eventNosqlEntityRepository.findByKind(
            kind.getValue()).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(IdentifierTag.class::isInstance)
                .map(IdentifierTag.class::cast)
                .collect(Collectors.toSet()).contains(identifierTag))
        .toList();
  }

  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey,
      @NonNull AddressTag addressTag) {
    return eventNosqlEntityRepository.findByKind(
            kind.getValue()).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(AddressTag.class::isInstance)
                .map(AddressTag.class::cast)
                .collect(Collectors.toSet()).contains(addressTag))
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(PubKeyTag.class::isInstance)
                .map(PubKeyTag.class::cast)
                .map(PubKeyTag::getPublicKey)
                .collect(Collectors.toSet()).contains(publicKey))
        .toList();
  }

  public List<EventNosqlEntityIF> getEventsByKindAndPubKeyTagAndExternalIdentityTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey,
      @NonNull ExternalIdentityTag externalIdentityTag) {
    return eventNosqlEntityRepository.findByKind(
            kind.getValue()).stream()
        .map(this::revertInterceptor)
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(ExternalIdentityTag.class::isInstance)
                .map(ExternalIdentityTag.class::cast)
                .collect(Collectors.toSet()).contains(externalIdentityTag))
        .filter(eventNosqlEntityIF ->
            eventNosqlEntityIF.getTags().stream()
                .filter(PubKeyTag.class::isInstance)
                .map(PubKeyTag.class::cast)
                .map(PubKeyTag::getPublicKey)
                .collect(Collectors.toSet()).contains(publicKey))
        .toList();
  }

  public List<EventNosqlEntityIF> getAll() {
    return eventNosqlEntityRepository.findAllCustom().stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public EventNosqlEntityIF saveEvent(@NonNull EventIF eventNosqlEntity) {
    return eventNosqlEntityRepository.save(convertDtoToNosqlEntity(eventNosqlEntity));
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
}
