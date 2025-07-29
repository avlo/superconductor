package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;
import com.prosilion.superconductor.lib.redis.dto.GenericDocumentKindDto;
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
  private final EventDocumentRepository<EventDocumentIF> eventDocumentRepository;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public EventDocumentService(
      @NonNull EventDocumentRepository<EventDocumentIF> eventDocumentRepository,
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    this.eventDocumentRepository = eventDocumentRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptor::getCode,
            Function.identity()));
    log.debug("Created EventDocumentService with interceptors:\n");
    interceptors.forEach(interceptor -> log.debug("  {}\n", interceptor));
  }

  public Optional<EventDocumentIF> findByEventIdString(@NonNull String eventIdString) {
    return eventDocumentRepository.findByEventIdString(eventIdString).map(this::revertInterceptor);
  }

  public List<EventDocumentIF> getEventsByKind(@NonNull Kind kind) {
    return eventDocumentRepository
        .findAllByKind(kind.getValue()).stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public Map<Kind, Map<String, EventDocumentIF>> getAllMappedByKind() {
    List<EventDocumentIF> eventDocumentRepositoryAll = getAll();
    return eventDocumentRepositoryAll
        .stream()
        .collect(
            Collectors.groupingBy(
                EventIF::getKind,
                Collectors.toMap(
                    EventDocumentIF::getEventId,
                    Function.identity(), (prev, next) -> next)));
  }

  public List<EventDocumentIF> getAll() {
    return eventDocumentRepository
        .findAll().stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public EventDocumentIF saveEventDocument(@NonNull BaseEvent baseEvent) {
    return saveEventDocument(new GenericDocumentKindDto(baseEvent).convertDtoToDocument());
  }

  public EventDocumentIF saveEventDocument(@NonNull EventIF eventDocument) {
    return saveEventDocument(convertDtoToDocument(eventDocument));
  }

  public EventDocumentIF saveEventDocument(@NonNull EventDocumentIF entity) {
    return eventDocumentRepository.save(
        entity);
  }

  private EventDocumentIF convertDtoToDocument(EventIF dto) {
    return processInterceptors(dto);
  }

  private EventDocumentIF processInterceptors(EventIF dto) {
    EventDocument eventDocument = EventDocument.of(
        dto.getEventId(),
        dto.getKind().getValue(),
        dto.getPublicKey().toString(),
        dto.getCreatedAt(),
        dto.getContent(),
        dto.getSignature().toString());

    eventDocument.setTags(Stream.concat(
        dto.getTags().stream().map(baseTag ->
            Optional.ofNullable(
                    interceptors.get(baseTag.getCode()))
                .stream().map(interceptor ->
                    (BaseTag) interceptor.intercept(baseTag)).toList()).flatMap(Collection::stream),
        dto.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode()))).toList());

    return eventDocument;
  }

  private EventDocumentIF revertInterceptor(EventDocumentIF documentToRevert) {
    EventDocument revertedDocument = EventDocument.of(
        documentToRevert.getEventId(),
        documentToRevert.getKind().getValue(),
        documentToRevert.getPublicKey().toString(),
        documentToRevert.getCreatedAt(),
        documentToRevert.getContent(),
        documentToRevert.getSignature().toString());

    revertedDocument.setTags(
        Stream.concat(
                documentToRevert.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            interceptor.canonicalize((RedisBaseTagIF) baseTag)).toList()).flatMap(Collection::stream),
                documentToRevert.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .collect(Collectors.toList()));

    return revertedDocument;
  }
}
