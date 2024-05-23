package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.RelaysTagDto;
import com.prosilion.superconductor.dto.ZapRequestDto;
import com.prosilion.superconductor.entity.ZapRequestEventEntity;
import com.prosilion.superconductor.repository.classified.ZapRequestEventEntityRepository;
import com.prosilion.superconductor.service.event.join.ZapRequestEventEntityEventEntityService;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nostr.base.ElementAttribute;
import nostr.base.PublicKey;
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
    log.info("processing incoming ZAP_REQUEST: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(57);
    event.setKind(Kind.ZAP_REQUEST.getValue());
    Long savedEventId = eventService.saveEventEntity(event);

    ZapRequestDto zapRequestEventDto = createZapRequestDto(event, getRelaysTag(event));
    ZapRequestEventEntity zapRequestEventEntity = saveZapRequestEvent(zapRequestEventDto);

    joinService.save(savedEventId, zapRequestEventEntity.getId());

    ZapRequestEvent zapRequestEvent = new ZapRequestEvent(
        event.getPubKey(),
        new PublicKey(zapRequestEventDto.getRecipientPubKey()),
        event.getTags(),
        event.getContent(),
        zapRequestEventDto
    );
    zapRequestEvent.setId(event.getId());
    zapRequestEvent.setCreatedAt(event.getCreatedAt());
    eventService.publishEvent(savedEventId, zapRequestEvent);
  }

  @SneakyThrows
  @NotNull
  private DiscoveredRelayTags getRelaysTag(GenericEvent event) {
    List<GenericTag> genericTagsOnly = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast).toList();

    List<List<ElementAttribute>> relaysTag = genericTagsOnly.stream()
        .filter(ZapRequestEventService::isZapRequestTag).map(GenericTag::getAttributes).toList();
    return new DiscoveredRelayTags(genericTagsOnly, relaysTag);
  }

  private static boolean isZapRequestTag(GenericTag tag) {
    return tag.getCode().equalsIgnoreCase("relays")
        || tag.getCode().equalsIgnoreCase("amount")
        || tag.getCode().equalsIgnoreCase("lnurl");
  }

  @NotNull
  private ZapRequestDto createZapRequestDto(GenericEvent event, DiscoveredRelayTags discoveredRelayTags) {
    return new ZapRequestDto(
        event.getPubKey().toString(),
        Long.valueOf(getReturnVal(discoveredRelayTags.genericTagsOnly(), "amount")),
        getReturnVal(discoveredRelayTags.genericTagsOnly(), "lnurl"),
        new RelaysTagDto(getReturnVal(discoveredRelayTags.genericTagsOnly(), "relays")));
  }

  private ZapRequestEventEntity saveZapRequestEvent(ZapRequestDto zapRequestEventDto) {
    return Optional.of(zapRequestEventEntityRepository.save(zapRequestEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }

  private static String getReturnVal(List<GenericTag> genericTags, String val) {
    return genericTags.stream()
        .filter(tag -> tag.getCode().equals(val)).findFirst().orElseThrow()
        .getAttributes().stream().map(ElementAttribute::getValue).findFirst().orElseThrow()
        .toString();
  }

  private record DiscoveredRelayTags(List<GenericTag> genericTagsOnly, List<List<ElementAttribute>> zapRequestDto) {
  }
}
