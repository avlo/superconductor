package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.RelaysTagDto;
import com.prosilion.superconductor.dto.ZapRequestDto;
import com.prosilion.superconductor.entity.classified.ZapRequestEventEntity;
import com.prosilion.superconductor.repository.classified.ZapRequestEventEntityRepository;
import com.prosilion.superconductor.service.event.join.ZapRequestEventEntityEventEntityService;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.base.ElementAttribute;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.impl.ZapRequestEvent;
import nostr.event.message.EventMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Getter
@Service
public class ZapRequestEventService<T extends EventMessage> implements EventServiceIF<T> {
  public final Kind kind = Kind.ZAP_REQUEST;
  private final EventService<ZapRequestEvent> eventService;
  private final ZapRequestEventEntityRepository zapRequestEventEntityRepository;
  private final ZapRequestEventEntityEventEntityService joinService;

  public ZapRequestEventService(
      EventService<ZapRequestEvent> eventService,
      ZapRequestEventEntityRepository zapRequestEventEntityRepository,
      ZapRequestEventEntityEventEntityService joinService) {
    this.eventService = eventService;
    this.zapRequestEventEntityRepository = zapRequestEventEntityRepository;
    this.joinService = joinService;
  }

  @Override
  @Async
  public void processIncoming(T eventMessage) {
    log.info("processing incoming TEXT_NOTE: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(57);
    event.setKind(Kind.ZAP_REQUEST.getValue());
    Long savedEventId = eventService.saveEventEntity(event);

    ZapRequestDto zapRequestDto = getZapRequestDto(event, createRelaysTagDto(event));
    ZapRequestEventEntity zapRequestEventEntity = saveZapRequestEvent(zapRequestDto);

    joinService.save(savedEventId, zapRequestEventEntity.getId());

    ZapRequestEvent zapRequestEvent = new ZapRequestEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent(),
        zapRequestDto
    );
    zapRequestEvent.setId(event.getId());
    zapRequestEvent.setCreatedAt(event.getCreatedAt());
    eventService.publishEvent(savedEventId, zapRequestEvent);
  }

  @NotNull
  private static Result createRelaysTagDto(GenericEvent event) {
    List<GenericTag> genericTagsOnly = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast).toList();

    List<List<ElementAttribute>> priceTagDto = genericTagsOnly.stream()
        .filter(tag -> tag.getCode().equalsIgnoreCase("relays")).map(GenericTag::getAttributes).toList();
    return new Result(genericTagsOnly, priceTagDto);
  }

  @NotNull
  private static ZapRequestDto getZapRequestDto(GenericEvent event, Result relaysTagDtoResult) {
    return new ZapRequestDto(
        RelaysTagDto.createRelaysTagDtoFromAttributes(relaysTagDtoResult.priceTagDto().stream().findFirst().orElseThrow()),
        Integer.valueOf(getReturnVal(relaysTagDtoResult.genericTagsOnly(), "amount")),
        getReturnVal(relaysTagDtoResult.genericTagsOnly(), "lnurl"),
        event.getPubKey()
    );
  }

  private ZapRequestEventEntity saveZapRequestEvent(ZapRequestDto zapRequestDto) {
    return Optional.of(zapRequestEventEntityRepository.save(zapRequestDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }

  private static String getReturnVal(List<GenericTag> genericTags, String val) {
    return genericTags.stream()
        .filter(tag -> tag.getCode().equals(val)).findFirst().orElseThrow()
        .getAttributes().stream().map(ElementAttribute::getValue).findFirst().orElseThrow()
        .toString();
  }

  private record Result(List<GenericTag> genericTagsOnly, List<List<ElementAttribute>> priceTagDto) {
  }
}
