package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
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

  public Optional<GenericEventKindIF> findByEventIdString(@NonNull String eventIdString) {
    return eventDocumentRepository
        .findByEventIdString(eventIdString)
        .map(this::revertInterceptor)
        .map(EventIF::convertEntityToDto);
  }

  public List<GenericEventKindIF> getEventsByKind(@NonNull Kind kind) {
    return eventDocumentRepository
        .findAllByKind(kind.getValue()).stream()
        .map(this::revertInterceptor)
        .map(EventIF::convertEntityToDto).toList();
  }

  public Map<Kind, Map<String, GenericEventKindIF>> getAll() {
    List<GenericEventKindIF> eventDocumentRepositoryAll = getEventDocumentRepositoryAll();
    return eventDocumentRepositoryAll
        .stream()
        .collect(
            Collectors.groupingBy(
                GenericEventKindIF::getKind,
                Collectors.toMap(
                    GenericEventKindIF::getId,
                    Function.identity(),
                    (prev, next) -> next)));
  }

  private List<GenericEventKindIF> getEventDocumentRepositoryAll() {
    return eventDocumentRepository
        .findAll().stream()
        .map(this::revertInterceptor)
        .map(EventIF::convertEntityToDto).toList();
  }

  public EventDocument saveEventDocument(@NonNull BaseEvent baseEvent) {
    return saveEventDocument(
        new GenericDocumentKindDto(baseEvent).convertBaseEventToGenericEventKindIF());
  }

  public EventDocument saveEventDocument(GenericEventKindIF genericEventKindIF) {
    return eventDocumentRepository.save(
        convertDtoToDocument(genericEventKindIF));
  }

  public EventDocument convertDtoToDocument(GenericEventKindIF dto) {
    return processInterceptors(
        dto,
        EventDocument.of(
            dto.getId(),
            dto.getKind().getValue(),
            dto.getPublicKey().toString(),
            dto.getCreatedAt(),
            dto.getContent(),
            dto.getSignature().toString()));
  }

  private EventDocument processInterceptors(GenericEventKindIF dto, EventDocument eventDocument) {
    eventDocument.setTags(
        Stream.concat(
                dto.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            (BaseTag) interceptor.intercept(baseTag)).toList()).flatMap(Collection::stream),
                dto.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .collect(Collectors.toList()));

    return eventDocument;
  }

  private EventDocument revertInterceptor(EventDocument documentToRevert) {
    EventDocument revertedDocument = EventDocument.of(
        documentToRevert.getEventIdString(),
        documentToRevert.getKind(),
        documentToRevert.getPubKey(),
        documentToRevert.getCreatedAt(),
        documentToRevert.getContent(),
        documentToRevert.getSignature());

    revertedDocument.setTags(
        Stream.concat(
                documentToRevert.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getCode()))
                        .stream().map(interceptor ->
                            interceptor.revert((RedisBaseTagIF) baseTag)).toList()).flatMap(Collection::stream),
                documentToRevert.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getCode())))
            .collect(Collectors.toList()));

    return revertedDocument;
  }
}
