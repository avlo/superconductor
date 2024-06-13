package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.classified.ClassifiedListingDto;
import com.prosilion.superconductor.dto.classified.PriceTagDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.message.EventMessage;
import nostr.event.tag.PriceTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClassifiedListingEventService<T extends EventMessage> implements EventServiceIF<T> {
  @Getter
  public final Kind kind = Kind.CLASSIFIED_LISTING;
  public static final String PUBLISHED_AT = "published_at";
  public static final String LOCATION = "location";
  public static final String SUMMARY = "summary";
  public static final String TITLE = "title";

  private final EventService<ClassifiedListingEvent> eventService;

  @Autowired
  public ClassifiedListingEventService(EventService<ClassifiedListingEvent> eventService) {
    this.eventService = eventService;
  }

  @Override
  @Async
  public void processIncoming(T eventMessage) {
    log.info("processing incoming CLASSIFIED_LISTING: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(99);

    ClassifiedListingDto classifiedListingDto = createClassifiedListingDto(event);
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
    eventService.publishEvent(savedEventId, classifiedListingEvent);
  }

  private ClassifiedListingDto createClassifiedListingDto(GenericEvent event) {
    DiscoveredClassifiedListingTag classifiedListingTags = getClassifiedListingSpecificGenericTags(event);

    ClassifiedListingDto classifiedListingDto = new ClassifiedListingDto(
        classifiedListingTags.map().get(TITLE),
        classifiedListingTags.map().get(SUMMARY),
        classifiedListingTags.map().get(LOCATION),
        getPriceTagDto(event)
    );

    classifiedListingDto.setPublishedAt(Long.valueOf(classifiedListingTags.map().get(PUBLISHED_AT)));
    return classifiedListingDto;
  }

  private DiscoveredClassifiedListingTag getClassifiedListingSpecificGenericTags(GenericEvent event) {
    return new DiscoveredClassifiedListingTag(event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(this::isClassifiedListingTag)
        .collect(
            Collectors.toMap(GenericTag::getCode, tag ->
                tag.getAttributes().stream()
                    .findAny()
                    .map(attr -> attr.getValue().toString()).orElseThrow())));
  }

  private boolean isClassifiedListingTag(GenericTag tag) {
    return List.of(TITLE, SUMMARY, PUBLISHED_AT, LOCATION).contains(tag.getCode());
  }

  private PriceTagDto getPriceTagDto(GenericEvent event) {
    Optional<PriceTagDto> first = event.getTags().stream()
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTagDto::new).findFirst();
    return Optional.of(first).get().orElseThrow();
  }

  private record DiscoveredClassifiedListingTag(Map<String, String> map) {
  }
}
