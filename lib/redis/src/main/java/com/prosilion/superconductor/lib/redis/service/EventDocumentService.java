package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;

public class EventDocumentService {
  private final EventDocumentRepository eventDocumentRepository;

  public EventDocumentService(EventDocumentRepository eventDocumentRepository) {
    this.eventDocumentRepository = eventDocumentRepository;
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
    List<GenericEventKindIF> eventEntityRepositoryAll = getEventEntityRepositoryAll();
    return eventEntityRepositoryAll
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

  public static EventDocument convertDtoToDocument(GenericEventKindIF dto) {
    EventDocument eventDocument = EventDocument.of(
        dto.getId(),
        dto.getKind().getValue(),
        dto.getPublicKey().toString(),
        dto.getCreatedAt(),
        dto.getContent(),
        dto.getSignature().toString());

    eventDocument.setTags(dto.getTags());
    return eventDocument;
  }
}
