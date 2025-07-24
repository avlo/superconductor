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
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class EventDocumentService<T extends BaseTag, U extends BaseTag> {
  private final EventDocumentRepository eventDocumentRepository;
  private final Map<Class<T>, TagInterceptorIF<T, U>> interceptors;

  public EventDocumentService(
      @NonNull EventDocumentRepository eventDocumentRepository,
      @NonNull List<TagInterceptorIF<T, U>> interceptors) {
    this.eventDocumentRepository = eventDocumentRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptorIF::getInterceptedType,
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
                            interceptors.get(baseTag.getClass()))
                        .stream().map(interceptor ->
                            interceptor.intercept((T) baseTag)).toList()).flatMap(Collection::stream),
                dto.getTags().stream().filter(baseTag -> !interceptors.containsKey(baseTag.getClass())))
            .collect(Collectors.toList()));

    return eventDocument;
  }

  private EventDocument revertInterceptor(EventDocument incomingDocument) {
    EventDocument morphedDocument = EventDocument.of(
        incomingDocument.getEventIdString(),
        incomingDocument.getKind(),
        incomingDocument.getPubKey(),
        incomingDocument.getCreatedAt(),
        incomingDocument.getContent(),
        incomingDocument.getSignature());

    List<BaseTag> incomingDocumentTags = incomingDocument.getTags();

    List<Class<U>> registeredInterceptors = interceptors.values()
        .stream().map(TagInterceptorIF::getMorphedType).toList();

    List<BaseTag> matchedInterceptors = registeredInterceptors.stream().flatMap(registeredInterceptor ->
        incomingDocumentTags.stream().filter(incomingTag -> incomingTag.getClass().equals(registeredInterceptor))).toList();

    Map<? extends Class<? extends BaseTag>, BaseTag> collect = matchedInterceptors.stream().collect(
        Collectors.toMap(
            BaseTag::getClass,
            Function.identity()));


    incomingDocument.getTags().removeAll(matchedInterceptors);

    List<BaseTag> collect1 = Stream.concat(
            incomingDocument.getTags().stream(),
            matchedInterceptors.stream().map(tagToMorph -> collect.get(tagToMorph.getClass())))
        .collect(Collectors.toList());

    morphedDocument.setTags(collect1);

    return morphedDocument;
  }
}
