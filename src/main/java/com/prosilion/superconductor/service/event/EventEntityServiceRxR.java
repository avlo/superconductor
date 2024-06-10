package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.EventDto;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.EventEntityRepository;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.service.event.join.standard.EventEntityStandardTagEntityServiceIFRxR;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventEntityServiceRxR<T extends GenericEvent> {
  private final EventEntityTagEntitiesServiceRxR<
      BaseTag,
      StandardTagEntityRepositoryRxR<StandardTagEntityRxR>,
      StandardTagEntityRxR,
      EventEntityStandardTagEntityRxR,
      EventEntityStandardTagEntityServiceIFRxR<StandardTagEntityRxR, EventEntityStandardTagEntityRxR>,
      EventEntityStandardTagEntityRepositoryRxR<EventEntityStandardTagEntityRxR>> eventEntityTagEntitiesServiceRxR;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityServiceRxR(
      EventEntityTagEntitiesServiceRxR<
          BaseTag,
          StandardTagEntityRepositoryRxR<StandardTagEntityRxR>,
          StandardTagEntityRxR,
          EventEntityStandardTagEntityRxR,
          EventEntityStandardTagEntityServiceIFRxR<StandardTagEntityRxR, EventEntityStandardTagEntityRxR>,
          EventEntityStandardTagEntityRepositoryRxR<EventEntityStandardTagEntityRxR>> eventEntityTagEntitiesServiceRxR,
      EventEntityRepository eventEntityRepository) {

    this.eventEntityTagEntitiesServiceRxR = eventEntityTagEntitiesServiceRxR;
    this.eventEntityRepository = eventEntityRepository;
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
    eventEntityTagEntitiesServiceRxR.saveTags(savedEntity.getId(), event.getTags());
    return savedEntity.getId();
  }

  public Map<Kind, Map<Long, T>> getAll() {
    return eventEntityRepository.findAll().parallelStream()
        .map(this::populateEventEntity)
        .collect(Collectors.groupingBy(eventEntity -> Kind.valueOf(eventEntity.getKind()),
            Collectors.toMap(EventEntity::getId, EventEntity::convertEntityToDto)));
  }

  public T getEventById(Long id) {
    return populateEventEntity(
        eventEntityRepository.findById(id).orElseThrow())
        .convertEntityToDto();
  }

  //  public GenericEvent getEventByEventIdString(String eventId) {
//    Optional<EventEntity> byEventId = eventEntityRepository.findByEventId(eventId);
//    return getEventByEventId(byEventId.get());
//  }

  private @NotNull EventEntity populateEventEntity(EventEntity eventEntity) {
    eventEntity.setTags(eventEntityTagEntitiesServiceRxR.getTags(eventEntity.getId()).parallelStream().map(StandardTagEntityRxR::getAsBaseTag).toList());
    return eventEntity;
  }
}
