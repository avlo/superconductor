package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.classified.ClassifiedListingDto;
import com.prosilion.superconductor.dto.classified.PriceTagDto;
import com.prosilion.superconductor.entity.classified.ClassifiedListingEventEntity;
import com.prosilion.superconductor.repository.classified.ClassifiedListingEntityRepository;
import com.prosilion.superconductor.service.event.join.classified.ClassifiedListingEntityEventEntityService;
import com.prosilion.superconductor.service.event.join.classified.PriceTagEntityService;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import nostr.base.ElementAttribute;
import nostr.event.Kind;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.message.EventMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClassifiedListingEventService<T extends EventMessage> implements EventServiceIF<T> {
  public final Kind kind = Kind.CLASSIFIED_LISTING;
  private final ClassifiedListingEntityRepository classifiedListingEntityRepository;
  private final ClassifiedListingEntityEventEntityService joinService;
  private final PriceTagEntityService priceTagEntityService;
  private final EventService<ClassifiedListingEvent> eventService;

  public ClassifiedListingEventService(
      EventService<ClassifiedListingEvent> eventService,
      ClassifiedListingEntityRepository classifiedListingEntityRepository,
      ClassifiedListingEntityEventEntityService joinService,
      PriceTagEntityService priceTagEntityService) {
    this.eventService = eventService;
    this.classifiedListingEntityRepository = classifiedListingEntityRepository;
    this.joinService = joinService;
    this.priceTagEntityService = priceTagEntityService;
  }

  @Override
  @Async
  public void processIncoming(T eventMessage) {
    log.info("processing incoming CLASSIFIED_LISTING: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(99);

    ClassifiedListingDto classifiedListingDto = createClassifiedListingDto(event);
    ClassifiedListingEventEntity classifiedListingEventEntity = saveClassifiedListing(classifiedListingDto);

    ClassifiedListingEvent classifiedListingEvent = new ClassifiedListingEvent(
        event.getPubKey(),
        Kind.valueOf(event.getKind()),
        event.getTags(),
        event.getContent(),
        classifiedListingDto);
    classifiedListingEvent.setId(event.getId());
    classifiedListingEvent.setCreatedAt(event.getCreatedAt());
    classifiedListingEvent.setSignature(event.getSignature());

    Long savedEventId = eventService.saveEventEntity(classifiedListingEvent);

    joinService.save(savedEventId, classifiedListingEventEntity.getId());
    priceTagEntityService.savePriceTag(savedEventId, classifiedListingDto.getPriceTag());

    eventService.publishEvent(savedEventId, classifiedListingEvent);
  }

  @Override
  public Kind getKind() {
    return kind;
  }

  private ClassifiedListingDto createClassifiedListingDto(GenericEvent event) {
    DiscoveredClassifiedListingTag classifiedListingTags = getClassifiedListingTags(event);
    DiscoveredPriceTagDto priceTag = getPriceTagDto(event);

    ClassifiedListingDto classifiedListingDto = new ClassifiedListingDto(
        classifiedListingTags.map().get("title"),
        classifiedListingTags.map().get("summary"),
        PriceTagDto.createPriceTagDtoFromAttributes(priceTag.priceTagDto().stream().findFirst().orElseThrow())
    );

    classifiedListingDto.setLocation(classifiedListingTags.map().get("location"));
    classifiedListingDto.setPublishedAt(Long.valueOf(classifiedListingTags.map().get("published_at")));
    return classifiedListingDto;
  }

  private ClassifiedListingEventEntity saveClassifiedListing(ClassifiedListingDto classifiedListingDto) {
    return Optional.of(classifiedListingEntityRepository.save(classifiedListingDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }

  private DiscoveredClassifiedListingTag getClassifiedListingTags(GenericEvent event) {
    return new DiscoveredClassifiedListingTag(event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(this::isClassifiedListingTag)
        .collect(
            Collectors.toMap(GenericTag::getCode, tag ->
                tag.getAttributes().stream()
                    .findFirst().map(attr -> attr.getValue().toString()).orElseThrow())));
  }

  private boolean isClassifiedListingTag(GenericTag tag) {
    return tag.getCode().equalsIgnoreCase("title")
        || tag.getCode().equalsIgnoreCase("summary")
        || tag.getCode().equalsIgnoreCase("published_at")
        || tag.getCode().equalsIgnoreCase("location");
  }

  private DiscoveredPriceTagDto getPriceTagDto(GenericEvent event) {
    List<GenericTag> genericTagsOnly = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast).toList();

    List<List<ElementAttribute>> priceTag = genericTagsOnly.stream()
        .filter(tag -> tag.getCode().equalsIgnoreCase("price")).map(GenericTag::getAttributes).toList();
    return new DiscoveredPriceTagDto(genericTagsOnly, priceTag);
  }

  public ClassifiedListingEventEntity findById(Long id) {
    return classifiedListingEntityRepository.findById(id).orElseThrow();
  }

  private record DiscoveredPriceTagDto(List<GenericTag> genericTagsOnly, List<List<ElementAttribute>> priceTagDto) {
  }

  private record DiscoveredClassifiedListingTag(Map<String, String> map) {
  }
}
