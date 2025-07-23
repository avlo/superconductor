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

    List<T> tags = (List<T>) dto.getTags();

    List<T> interceptedTags = tags.stream().map(baseTag ->
    {
      List<InterceptorIF<T>> list = Optional.ofNullable(interceptors.get(baseTag.getClass())).stream().toList();
      Stream<T> baseTagStream = (Stream<T>) list.stream().map(interceptor ->
          interceptor.intercept(baseTag));
      return baseTagStream.toList();
    }).flatMap(Collection::stream).toList();

    List<BaseTag> uninterceptedTags = dto.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getClass())).toList();

    EventDocument eventDocument = EventDocument.of(
        dto.getId(),
        dto.getKind().getValue(),
        dto.getPublicKey().toString(),
        dto.getCreatedAt(),
        dto.getContent(),
        dto.getSignature().toString());

    List<BaseTag> merged = Stream.concat(
            interceptedTags.stream(),
            uninterceptedTags.stream())
        .collect(Collectors.toList());

    eventDocument.setTags(merged);
    return eventDocument;
  }
}
