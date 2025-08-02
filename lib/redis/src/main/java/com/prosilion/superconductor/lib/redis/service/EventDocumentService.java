package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
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
    Optional<EventDocumentIF> eventDocumentIF = eventDocumentRepository.findByEventId(eventId).map(this::revertInterceptor);
    return eventDocumentIF;
  }

  public List<EventDocumentIF> getEventsByKind(@NonNull Kind kind) {
    List<EventDocumentIF> list = eventDocumentRepository
        .findByKind(kind.getValue()).stream()
        .map(this::revertInterceptor)
        .toList();
    return list;
  }

  public List<EventDocumentIF> getAll() {
    List<EventDocumentIF> list = eventDocumentRepository
        .findAllCustom().stream()
        .map(this::revertInterceptor)
        .toList();
    return list;
  }

  public EventDocumentIF saveEventDocument(@NonNull EventIF eventDocument) {
    EventDocument eventDocumentIF = convertDtoToDocument(eventDocument);
    EventDocument save = eventDocumentRepository.save(eventDocumentIF);
    return save;
  }

  private EventDocument convertDtoToDocument(EventIF dto) {
    return processInterceptors(dto);
  }

  private EventDocument processInterceptors(EventIF dto) {
    EventDocument eventDocument = EventDocument.of(
        dto.getId(),
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

  protected EventDocumentIF revertInterceptor(EventDocumentIF documentToRevert) {
    EventDocument revertedDocument = EventDocument.of(
        documentToRevert.getId(),
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
