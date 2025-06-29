package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.service.event.service.EventKindTypeServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService implements EventServiceIF {
  private final EventKindServiceIF<Kind> eventKindService;
  private final EventKindTypeServiceIF<KindTypeIF> eventKindTypeService;

  @Autowired
  public EventService(@NonNull EventKindServiceIF<Kind> eventKindService, @NonNull EventKindTypeServiceIF<KindTypeIF> eventKindTypeService) {
    this.eventKindService = eventKindService;
    this.eventKindTypeService = eventKindTypeService;
  }

  //  TODO: stream variant, check works proper and replace OG
//  public void processIncomingEventRxR(@NonNull EventMessage eventMessage) {
//    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);
//    KindType[] kindTypes = KindTypeIF.kindTypes;
//    Kind kind = eventMessage.getEvent().getKind();
//
//    Stream.of(kindTypes)
//        .map(KindType::getKind)
//        .filter(kind::equals)
//        .findFirst()
//        .ifPresentOrElse(
//            kindType ->
//                eventKindTypeService.processIncomingKindTypeEvent((GenericEventKindTypeIF) eventMessage.getEvent()),
//            () ->
//                eventKindService.processIncomingEvent(eventMessage.getEvent()));
//  }

  @Override
  public void processIncomingEvent(@NonNull EventMessage eventMessage) {
    log.debug("processing incoming TEXT_NOTE: [{}]", eventMessage);

    if (eventKindService.getKinds().stream().anyMatch(eventMessage.getEvent().getKind()::equals)) {
      eventKindService.processIncomingEvent(eventMessage.getEvent());
      return;
    }

    GenericEventKindIF genericEventKindIF = eventMessage.getEvent();
    GenericEventKindTypeIF genericEventKindType = (GenericEventKindTypeIF) genericEventKindIF;
    eventKindTypeService.processIncomingEvent(genericEventKindType);
  }
}
