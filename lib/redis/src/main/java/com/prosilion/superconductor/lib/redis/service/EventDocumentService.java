package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.base.EventEntityIF;
import com.prosilion.superconductor.base.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.repository.EventDocumentRepository;
import java.util.Optional;
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

  public Optional<EventEntityIF> findByEventIdString(@NonNull String eventIdString) {
    return eventDocumentRepository.findByEventIdString(eventIdString).map(EventIF::convertEntityToDto);
  }

  public EventEntityIF saveEventDocument(GenericEventKindIF genericEventKindIF) {
    return eventDocumentRepository.save(convertDtoToDocument(genericEventKindIF)).convertEntityToDto();
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
