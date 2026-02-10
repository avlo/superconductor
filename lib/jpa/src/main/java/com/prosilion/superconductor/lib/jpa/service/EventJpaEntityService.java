package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.EntityServiceIF;
import com.prosilion.superconductor.lib.jpa.dto.generic.ElementAttributeDto;
import com.prosilion.superconductor.lib.jpa.dto.generic.GenericTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.EventJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;

@Slf4j
public class EventJpaEntityService implements EntityServiceIF<Long, EventJpaEntityIF> {
  private final ConcreteTagEntitiesService<
      BaseTag,
      AbstractTagJpaEntityRepository<AbstractTagJpaEntity>,
      AbstractTagJpaEntity,
      EventEntityAbstractJpaEntity,
      EventEntityAbstractTagJpaEntityRepository<EventEntityAbstractJpaEntity>> concreteTagEntitiesService;
  private final GenericTagJpaEntitiesService genericTagJpaEntitiesService;
  private final EventJpaEntityRepository eventJpaEntityRepository;

  public EventJpaEntityService(
      @NonNull ConcreteTagEntitiesService<
          BaseTag,
          AbstractTagJpaEntityRepository<AbstractTagJpaEntity>,
          AbstractTagJpaEntity,
          EventEntityAbstractJpaEntity,
          EventEntityAbstractTagJpaEntityRepository<EventEntityAbstractJpaEntity>> concreteTagEntitiesService,
      @NonNull GenericTagJpaEntitiesService genericTagJpaEntitiesService,
      @NonNull EventJpaEntityRepository eventJpaEntityRepository) {
    this.concreteTagEntitiesService = concreteTagEntitiesService;
    this.genericTagJpaEntitiesService = genericTagJpaEntitiesService;
    this.eventJpaEntityRepository = eventJpaEntityRepository;
  }

  @Override
  public Long save(@NonNull EventIF genericEventKindIF) {
    try {
      Long savedEntityId = eventJpaEntityRepository.save(
              convertDtoToEntity(genericEventKindIF))
          .getUid();

      concreteTagEntitiesService.saveTags(savedEntityId, genericEventKindIF.getTags());
      genericTagJpaEntitiesService.saveGenericTags(savedEntityId, genericEventKindIF.getTags());
      return savedEntityId;
    } catch (DataIntegrityViolationException e) {
      log.debug("Duplicate eventIdString on save(), returning existing EventEntity");
      return eventJpaEntityRepository.findByEventId(genericEventKindIF.getId()).orElseThrow(NoResultException::new).getUid();
    }
  }

  @Override
  public List<EventJpaEntityIF> getAll() {
    List<EventJpaEntityIF> entities = new ArrayList<>(eventJpaEntityRepository.findAll());
    return populateEventJpaEntities(entities);
  }

  @Override
  public Optional<EventJpaEntityIF> findByEventIdString(@NonNull String eventIdString) {
    Optional<EventJpaEntityIF> entityIF = eventJpaEntityRepository
        .findByEventId(eventIdString);
    return entityIF
        .map(this::populateEventJpaEntity);
  }

  public List<EventJpaEntityIF> getEventsByPublicKey(@NonNull PublicKey publicKey) {
    return eventJpaEntityRepository.findByPubKey(
            publicKey.toHexString()).stream()
        .map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKind(@NonNull Kind kind) {
    return eventJpaEntityRepository.findByKind(kind.getValue())
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return eventJpaEntityRepository.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey)
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndPubKeyTag(Kind kind, PublicKey referencePubKeyTag) {
    return eventJpaEntityRepository.getEventsByKindAndPubKeyTag(kind, referencePubKeyTag)
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(@NonNull Kind kind, @NonNull PublicKey referencePubKeyTag, @NonNull AddressTag addressTag) {
    return eventJpaEntityRepository.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag)
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag) {
    return eventJpaEntityRepository.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencedPubkeyTag, identifierTag)
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    return eventJpaEntityRepository.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag)
        .stream().map(ee ->
            getEventByUid(ee.getUid())).flatMap(Optional::stream).toList();
  }

  //  TODO: this is a 2nd call to db, needs economic sol'n
  public Optional<EventJpaEntityIF> getEventByUid(@NonNull Long id) {
    return eventJpaEntityRepository.findByUid(id).map(this::populateEventJpaEntity);
  }

  //  TODO: perhaps below as admin fxnality
  protected void deleteEventEntity(@NonNull EventJpaEntityIF eventToDelete) {
//    concreteTagEntitiesService.deleteTags(eventToDelete.getUid(), eventToDelete.getTags());
//    genericTagEntitiesService.deleteTags(eventToDelete.getTags());
//    eventEntityRepository.delete(convertDtoToEntity(eventToDelete));
  }

  private List<EventJpaEntityIF> populateEventJpaEntities(List<EventJpaEntityIF> entities) {
    List<Long> eventIds = entities.stream().map(EventJpaEntityIF::getUid).toList();

    Map<Long, List<AbstractTagJpaEntity>> concreteTags =
        concreteTagEntitiesService.getTagsByEventIds(eventIds);
    Map<Long, List<GenericTagDto>> genericTags =
        genericTagJpaEntitiesService.getGenericTagsByEventIds(eventIds);

    entities.forEach(entity -> entity.setTags(
        Stream.concat(
            concreteTags.getOrDefault(entity.getUid(), List.of()).stream()
                .map(AbstractTagJpaEntity::getAsBaseTag),
            genericTags.getOrDefault(entity.getUid(), List.of()).stream()
                .map(genericTag -> (BaseTag) new GenericTag(
                    genericTag.code(),
                    genericTag.atts().stream()
                        .map(ElementAttributeDto::getElementAttribute).toList()))
        ).toList()));
    return entities;
  }

  //  TODO: this is a 2nd call to db, needs economic sol'n
  private EventJpaEntityIF populateEventJpaEntity(EventJpaEntityIF entityIF) {
    entityIF.setTags(
        Stream.concat(
            concreteTagEntitiesService.getTags(entityIF.getUid()).stream().map(AbstractTagJpaEntity::getAsBaseTag).toList().stream(),
            genericTagJpaEntitiesService.getGenericTags(entityIF.getUid()).stream().map(genericTag ->
                    new GenericTag(
                        genericTag.code(),
                        genericTag.atts().stream().map(ElementAttributeDto::getElementAttribute).toList())).toList().stream()
                .map(BaseTag.class::cast).toList().stream()).toList());
    return entityIF;
  }

  private EventJpaEntity convertDtoToEntity(EventIF dto) {
    return new EventJpaEntity(dto.getId(), dto.getKind().getValue(), dto.getPublicKey().toString(), dto.getCreatedAt(), dto.getSignature().toString(), dto.getContent());
  }
}
