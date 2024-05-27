package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.repository.EventEntityRepository;
import com.prosilion.superconductor.service.NotifierService;
import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Getter
@Service
public class EventService<T extends GenericEvent> {
  private final EventEntityTagEntityService eventEntityTagEntityService;
  private final EventEntityRepository eventEntityRepository;
  private final NotifierService<T> notifierService;

  @Autowired
  public EventService(
      EventEntityTagEntityService eventEntityTagEntityService,
      EventEntityRepository eventEntityRepository,
      NotifierService<T> notifierService) {
    this.eventEntityTagEntityService = eventEntityTagEntityService;
    this.eventEntityRepository = eventEntityRepository;
    this.notifierService = notifierService;
  }

  protected Long saveEventEntity(GenericEvent event) {
    EventDto eventToSave = new EventDto(
        event.getPubKey(),
        event.getId(),
        Kind.valueOf(event.getKind()),
        event.getNip(),
        event.getCreatedAt(),
        event.getSignature(),
        event.getTags(),
        event.getContent()
    );

    EventEntity savedEntity = Optional.of(eventEntityRepository.save(eventToSave.convertDtoToEntity())).orElseThrow(NoResultException::new);
    eventEntityTagEntityService.saveTags(event, savedEntity.getId());
    return savedEntity.getId();
  }

  public Optional<EventEntity> getEvent(Long id) {
    return eventEntityRepository.findById(id);
  }

  protected void publishEvent(Long id, T event) {
    notifierService.nostrEventHandler(new AddNostrEvent<>(Kind.valueOf(event.getKind()), id, event));
  }
}
