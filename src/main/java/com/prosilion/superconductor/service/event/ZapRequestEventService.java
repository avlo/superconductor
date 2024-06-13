package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.dto.zap.ZapRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.impl.ZapRequestEvent;
import nostr.event.message.EventMessage;
import nostr.event.tag.PubKeyTag;
import nostr.event.tag.RelaysTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZapRequestEventService<T extends EventMessage> implements EventServiceIF<T> {
  @Getter
  public final Kind kind = Kind.ZAP_REQUEST;
  public static final String RELAYS = "relays";
  public static final String AMOUNT = "amount";
  public static final String LNURL = "lnurl";

  private final EventService<ZapRequestEvent> eventService;

  @Autowired
  public ZapRequestEventService(EventService<ZapRequestEvent> eventService) {
    this.eventService = eventService;
  }

  @Override
  @Async
  public void processIncoming(T eventMessage) {
    log.info("processing incoming ZAP_REQUEST: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    event.setNip(57);
    event.setKind(Kind.ZAP_REQUEST.getValue());

    ZapRequestDto zapRequestEventDto = createZapRequestDto(event);
    ZapRequestEvent zapRequestEvent = new ZapRequestEvent(
        event.getPubKey(),
        new PubKeyTag(new PublicKey(zapRequestEventDto.getRecipientPubKey())),
        event.getTags(),
        event.getContent(),
        zapRequestEventDto
    );
    zapRequestEvent.setId(event.getId());
    zapRequestEvent.setCreatedAt(event.getCreatedAt());
    zapRequestEvent.setSignature(event.getSignature());

    Long savedEventId = eventService.saveEventEntity(zapRequestEvent);
    eventService.publishEvent(savedEventId, zapRequestEvent);
  }


  @NotNull
  private ZapRequestDto createZapRequestDto(GenericEvent event) {
    DiscoveredZapRequestTags discoveredZapRequestTags = getZapRequestTags(event);

    return new ZapRequestDto(
        event.getPubKey().toString(),
        Long.valueOf(discoveredZapRequestTags.map().get(AMOUNT)),
        discoveredZapRequestTags.map().get(LNURL),
        getRelaysTagDto(event));
  }


  private DiscoveredZapRequestTags getZapRequestTags(GenericEvent event) {
    return new DiscoveredZapRequestTags(event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(this::isZapRequestTag)
        .collect(
            Collectors.toMap(GenericTag::getCode, tag ->
                tag.getAttributes().stream()
                    .findAny()
                    .map(attr -> attr.getValue().toString()).orElseThrow())));
  }

  private boolean isZapRequestTag(GenericTag tag) {
    return List.of(RELAYS, AMOUNT, LNURL).contains(tag.getCode());
  }

  private RelaysTagDto getRelaysTagDto(GenericEvent event) {
    Optional<RelaysTagDto> first = event.getTags().stream()
        .filter(RelaysTag.class::isInstance)
        .map(RelaysTag.class::cast)
        .map(RelaysTagDto::new).findFirst();
    return Optional.of(first).get().orElseThrow();
  }

  private record DiscoveredZapRequestTags(Map<String, String> map) {
  }
}
