package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class EventDocumentService {
  private final EventDocumentRepository eventDocumentRepository;

  @Autowired
  public EventDocumentService(EventDocumentRepository eventDocumentRepository) {
    this.eventDocumentRepository = eventDocumentRepository;
  }

  public Optional<EventIF> findByEventIdString(@NonNull String eventIdString) {
    return eventDocumentRepository.findByEventIdString(eventIdString).map(EventIF::convertEntityToDto);
  }

  public Map<Kind, Map<String, GenericEventKindIF>> getAll() {
    return getEventEntityRepositoryAll().stream()
        .collect(
            Collectors.groupingBy(eventEntity ->
                    Kind.valueOf(eventEntity.getKind()),
                Collectors.toMap(
                    EventIF::getEventIdString,
                    EventIF::convertEntityToDto)));
  }

  private List<EventIF> getEventEntityRepositoryAll() {
    return eventDocumentRepository.findAll().stream()
        .map(EventIF::convertEntityToDto)
        .map(EventIF.class::cast)
        .toList();
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
