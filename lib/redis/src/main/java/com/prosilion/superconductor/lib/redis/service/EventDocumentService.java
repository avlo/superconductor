package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import com.prosilion.superconductor.lib.redis.taginterceptor.InterceptorIF;
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
  private final Map<Class<T>, InterceptorIF<T>> interceptors;

  public EventDocumentService(
      @NonNull EventDocumentRepository eventDocumentRepository,
      @NonNull List<InterceptorIF<T>> interceptors) {
    this.eventDocumentRepository = eventDocumentRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            InterceptorIF::getInterceptorType,
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
    return getEventEntityRepositoryAll()
        .stream()
        .collect(
            Collectors.groupingBy(
                GenericEventKindIF::getKind,
                Collectors.toMap(
                    GenericEventKindIF::getId,
                    Function.identity(),
                    (prev, next) -> next)));
  }

  private List<GenericEventKindIF> getEventEntityRepositoryAll() {
    return eventDocumentRepository
        .findAll()
        .stream()
        .map(EventIF::convertEntityToDto).toList();
  }

  public void saveEventDocument(GenericEventKindIF genericEventKindIF) {
    eventDocumentRepository.save(
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
