package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import com.prosilion.superconductor.lib.redis.interceptor.RedisBaseTagIF;
import com.prosilion.superconductor.lib.redis.interceptor.TagInterceptor;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
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
public class EventDocumentService {
  private final EventDocumentRepository eventDocumentRepository;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public EventDocumentService(
      @NonNull EventDocumentRepository eventDocumentRepository,
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    this.eventDocumentRepository = eventDocumentRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptor::getCode,
            Function.identity()));
    log.debug("Created EventDocumentService with interceptors:\n");
    interceptors.forEach(interceptor -> log.debug("  {}\n", interceptor));
  }

  public Optional<EventDocumentIF> findByEventIdString(@NonNull String eventId) {
    return eventDocumentRepository.findByEventId(eventId)
        .map(this::revertInterceptor);
  }

  public List<EventDocumentIF> getEventsByKind(@NonNull Kind kind) {
    return eventDocumentRepository.findByKind(kind.getValue()).stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public List<EventDocumentIF> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    return eventDocumentRepository.findByKind(
            kind.getValue()).stream()
        .map(this::revertInterceptor)
        .filter(eventDocumentIF ->
            eventDocumentIF.getTags().stream()
                .filter(PubKeyTag.class::isInstance)
                .map(PubKeyTag.class::cast)
                .map(PubKeyTag::getPublicKey)
                .collect(Collectors.toSet()).contains(publicKey))
        .toList();
  }

  public List<EventDocumentIF> getEventsByKindAndUuid(
      @NonNull Kind kind,
      @NonNull String uuid) {
    return eventDocumentRepository.findByKind(
            kind.getValue()).stream()
        .map(this::revertInterceptor)
        .filter(eventDocumentIF ->
            eventDocumentIF.getTags().stream()
                .filter(IdentifierTag.class::isInstance)
                .map(IdentifierTag.class::cast)
                .map(IdentifierTag::getUuid)
                .collect(Collectors.toSet()).contains(uuid))
        .toList();
  }

  public List<EventDocumentIF> getAll() {
    return eventDocumentRepository.findAllCustom().stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public EventDocumentIF saveEventDocument(@NonNull EventIF eventDocument) {
    return eventDocumentRepository.save(convertDtoToDocument(eventDocument));
  }

  private EventDocument convertDtoToDocument(EventIF dto) {
    return processInterceptors(dto);
  }

  //  TODO: functionalize below two methods into one
  private EventDocument processInterceptors(EventIF eventIF) {
    EventDocument returnDocument = EventDocument.of(
        eventIF.getId(),
        eventIF.getKind().getValue(),
        eventIF.getPublicKey().toString(),
        eventIF.getCreatedAt(),
        eventIF.getContent(),
        eventIF.getSignature().toString());

    returnDocument.setTags(
        Stream.concat(
                eventIF.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            (BaseTag) interceptor.intercept(baseTag)).toList()).flatMap(Collection::stream),
                eventIF.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .toList());

    return returnDocument;
  }

  protected EventDocumentIF revertInterceptor(EventDocumentIF eventIf) {
    EventDocument returnDocument = EventDocument.of(
        eventIf.getId(),
        eventIf.getKind().getValue(),
        eventIf.getPublicKey().toString(),
        eventIf.getCreatedAt(),
        eventIf.getContent(),
        eventIf.getSignature().toString());

    returnDocument.setTags(
        Stream.concat(
                eventIf.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            interceptor.canonicalize((RedisBaseTagIF) baseTag)).toList()).flatMap(Collection::stream),
                eventIf.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .toList());

    return returnDocument;
  }
}
