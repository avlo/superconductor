package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.NonNull;

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
    return populateEventJpaEntities(
        Collections.unmodifiableList(
            eventJpaEntityRepository.findAll()));
  }

  private final Function<List<EventJpaEntityIF>, Stream<EventJpaEntityIF>> populateFxn =
      eventJpaEntityIFs -> eventJpaEntityIFs.stream().map(this::populateEventJpaEntity);

  @Override
  public Optional<EventJpaEntityIF> findByEventIdString(@NonNull String eventIdString) {
    return populateFxn.apply(eventJpaEntityRepository
        .findByEventId(eventIdString).stream().toList()).findFirst();
  }

  public List<EventJpaEntityIF> getEventsByPublicKey(@NonNull PublicKey publicKey) {
    return populateFxn.apply(eventJpaEntityRepository
        .findByPubKey(publicKey.toHexString())).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKind(@NonNull Kind kind) {
    return populateFxn.apply(eventJpaEntityRepository
        .findByKind(kind.getValue())).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return populateFxn.apply(eventJpaEntityRepository
        .getEventsByKindAndAuthorPublicKey(kind, authorPublicKey)).toList();
  }

  private final BiFunction<BaseTag, Stream<EventJpaEntityIF>, Stream<EventJpaEntityIF>> typedTagFxn =
      (baseTag, eventJpaEntityIFs) ->
          eventJpaEntityIFs.filter(entity -> containsTypedTargetTag(baseTag, entity));

  @Override
  public Optional<EventJpaEntityIF> getEventByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    return typedTagFxn.apply(identifierTag,
        getEventsByKindAndAuthorPublicKey(kind, authorPublicKey).stream()).findFirst();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndPubKeyTag(Kind kind, PubKeyTag pubKeyTag) {
    return typedTagFxn.apply(pubKeyTag,
        populateFxn.apply(eventJpaEntityRepository
            .getEventsByKindAndPubKeyTag(kind, pubKeyTag))).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndAddressTag(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    return typedTagFxn.apply(addressTag,
        populateFxn.apply(eventJpaEntityRepository
            .getEventsByKindAndAddressTag(kind, addressTag))).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndAddressTag(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return typedTagFxn.apply(addressTag, getEventsByKindAndPubKeyTag(kind, pubKeyTag).stream()).toList();
  }

  @Override
  public List<EventJpaEntityIF> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PubKeyTag pubKeyTag, IdentifierTag identifierTag) {
    return typedTagFxn.apply(identifierTag, getEventsByKindAndPubKeyTag(kind, pubKeyTag).stream()).toList();
  }

  //  TODO: below called after save() (which returns a Long instead of EventJpaEntityIF)
  //    resulting in a 2nd db call (in addition to the original save()), needs economic sol'n
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
            concreteTagEntitiesService.getTags(entityIF.getUid()).stream().map(AbstractTagJpaEntity::getAsBaseTag),
            genericTagJpaEntitiesService.getGenericTags(entityIF.getUid()).stream().map(genericTag ->
                    new GenericTag(
                        genericTag.code(),
                        genericTag.atts().stream().map(ElementAttributeDto::getElementAttribute).toList()))
                .map(BaseTag.class::cast)).toList());
    return entityIF;
  }

  private EventJpaEntity convertDtoToEntity(EventIF dto) {
    return new EventJpaEntity(dto.getId(), dto.getKind().getValue(), dto.getPublicKey().toString(), dto.getCreatedAt(), dto.getSignature().toString(), dto.getContent());
  }

  private <T extends BaseTag> boolean containsTypedTargetTag(T targetTagType, EventJpaEntityIF eventJpaEntityIF) {
    return eventJpaEntityIF
        .findFirstTag(targetTagType.getClass()).stream()
        .map(targetTagType::equals)
        .findAny().isPresent();
  }
}
