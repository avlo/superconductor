package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.dto.GenericDocumentKindDto;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import com.prosilion.superconductor.lib.redis.taginterceptor.TagInterceptorIF;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.lang.NonNull;

public class EventDocumentService<T extends BaseTag> {
  private final EventDocumentRepository eventDocumentRepository;
  private final Map<Class<T>, TagInterceptorIF<T>> interceptors;

  public EventDocumentService(
      @NonNull EventDocumentRepository eventDocumentRepository,
      @NonNull List<TagInterceptorIF<T>> interceptors) {
    this.eventDocumentRepository = eventDocumentRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptorIF::getInterceptorType,
            Function.identity()));
  }

  public Optional<GenericEventKindIF> findByEventIdString(@NonNull String eventIdString) {
    return eventDocumentRepository
        .findByEventIdString(eventIdString)
        .map(EventIF::convertEntityToDto);
  }

  public List<GenericEventKindIF> getEventsByKind(@NonNull Kind kind) {
    return eventDocumentRepository
        .findAllByKind(kind.getValue())
        .stream().map(EventIF::convertEntityToDto).toList();
  }

  public Map<Kind, Map<String, GenericEventKindIF>> getAll() {
    return getEventDocumentRepositoryAll()
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
        .findAll()
        .stream()
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
    EventDocument eventDocument = EventDocument.of(
        dto.getId(),
        dto.getKind().getValue(),
        dto.getPublicKey().toString(),
        dto.getCreatedAt(),
        dto.getContent(),
        dto.getSignature().toString());

    eventDocument.setTags(
        Stream.concat(
                dto.getTags().stream().map(baseTag ->
                    Optional.ofNullable(
                            interceptors.get(baseTag.getClass()))
                        .stream().map(interceptor ->
                            interceptor.intercept((T) baseTag)).toList()).flatMap(Collection::stream),
                dto.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getClass())))
            .collect(Collectors.toList()));

    return eventDocument;
  }
}
