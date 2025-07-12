package prosilion.superconductor.lib.jpa.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.EventEntityIF;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import prosilion.superconductor.lib.jpa.entity.EventEntity;
import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import prosilion.superconductor.lib.jpa.event.join.generic.GenericTagEntitiesService;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.EventEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import prosilion.superconductor.lib.jpa.service.ConcreteTagEntitiesService;

@Slf4j
@Service
public class EventEntityService {
  private final ConcreteTagEntitiesService<BaseTag, AbstractTagEntityRepository<AbstractTagEntity>, AbstractTagEntity, EventEntityAbstractEntity, EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>> concreteTagEntitiesService;
  private final GenericTagEntitiesService genericTagEntitiesService;
  private final EventEntityRepository eventEntityRepository;

  @Autowired
  public EventEntityService(ConcreteTagEntitiesService<BaseTag, AbstractTagEntityRepository<AbstractTagEntity>, AbstractTagEntity, EventEntityAbstractEntity, EventEntityAbstractTagEntityRepository<EventEntityAbstractEntity>> concreteTagEntitiesService, GenericTagEntitiesService genericTagEntitiesService, EventEntityRepository eventEntityRepository) {

    this.concreteTagEntitiesService = concreteTagEntitiesService;
    this.genericTagEntitiesService = genericTagEntitiesService;
    this.eventEntityRepository = eventEntityRepository;
  }

  public Long saveEventEntity(@NonNull BaseEvent event) {
    try {
      EventEntityIF savedEntity = Optional.of(eventEntityRepository.save(new GenericEventKindDto(event).convertDtoToEntity())).orElseThrow(NoResultException::new);
      concreteTagEntitiesService.saveTags(savedEntity.getId(), event.getTags());
      genericTagEntitiesService.saveGenericTags(savedEntity.getId(), event.getTags());
      return savedEntity.getId();
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventEntityRepository.findByEventIdString(event.getId()).orElseThrow(NoResultException::new).getId();
    }
  }

  public Long saveEventEntity(@NonNull GenericEventKindIF event) {
    try {
      EventEntityIF savedEntity = Optional.of(eventEntityRepository.save(convertDtoToEntity(event))).orElseThrow(NoResultException::new);
      concreteTagEntitiesService.saveTags(savedEntity.getId(), event.getTags());
      genericTagEntitiesService.saveGenericTags(savedEntity.getId(), event.getTags());
      return savedEntity.getId();
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventEntityRepository.findByEventIdString(event.getId()).orElseThrow(NoResultException::new).getId();
    }
  }

  public Map<Kind, Map<Long, GenericEventKindIF>> getAll() {
    return getEventEntityRepositoryAll().stream().map(this::populateEventEntity).collect(Collectors.groupingBy(eventEntity -> Kind.valueOf(eventEntity.getKind()), Collectors.toMap(EventEntityIF::getId, EventEntityIF::convertEntityToDto)));
  }

  private List<EventEntityIF> getEventEntityRepositoryAll() {
    return eventEntityRepository.findAll(EventEntityIF.class);
  }

  public Optional<EventEntityIF> findByEventIdString(@NonNull String eventIdString) {
    return eventEntityRepository.findByEventIdString(eventIdString);
  }

  public List<GenericEventKindIF> getEventsByPublicKey(@NonNull PublicKey publicKey) {
    return eventEntityRepository.findByPubKey(publicKey.toHexString()).stream().map(ee -> getEventById(ee.getId())).toList();
  }

  public List<GenericEventKindIF> getEventsByKind(@NonNull Kind kind) {
    return eventEntityRepository.findByKind(kind.getValue()).stream().map(ee -> getEventById(ee.getId())).toList();
  }

  public GenericEventKindIF getEventById(@NonNull Long id) {
    return populateEventEntity(getById(id).orElseThrow()).convertEntityToDto();
  }

  //  TODO: perhaps below as admin fxnality
  protected void deleteEventEntity(@NonNull EventEntityIF eventToDelete) {
//    concreteTagEntitiesService.deleteTags(eventToDelete.getId(), eventToDelete.getTags());
    genericTagEntitiesService.deleteTags(eventToDelete.getTags());
    eventEntityRepository.delete((EventEntity) eventToDelete);
  }

  private Optional<EventEntityIF> getById(Long id) {
    return eventEntityRepository.findById(EventEntityIF.class, id);
  }

  private EventEntityIF populateEventEntity(EventEntityIF eventEntity) {
    List<BaseTag> concreteTags = concreteTagEntitiesService.getTags(eventEntity.getId()).stream().map(AbstractTagEntity::getAsBaseTag).toList();

    List<BaseTag> genericTags = genericTagEntitiesService.getGenericTags(eventEntity.getId()).stream().map(genericTag -> new GenericTag(genericTag.code(), genericTag.atts().stream().map(ElementAttributeDto::getElementAttribute).toList())).toList().stream().map(BaseTag.class::cast).toList();

    eventEntity.setTags(Stream.concat(concreteTags.stream(), genericTags.stream()).toList());
    return eventEntity;
  }

  public static EventEntity convertDtoToEntity(GenericEventKindIF dto) {
    return new EventEntity(dto.getId(), dto.getKind().getValue(), dto.getPublicKey().toString(), dto.getCreatedAt(), dto.getSignature().toString(), dto.getContent());
  }
}
