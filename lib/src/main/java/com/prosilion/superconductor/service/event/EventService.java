package com.prosilion.superconductor.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
import com.prosilion.superconductor.service.event.service.EventKindTypeServiceIF;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService implements EventServiceIF {
  private final EventKindServiceIF eventKindService;
  private final EventKindTypeServiceIF eventKindTypeService;

  @Autowired
  public EventService(@NonNull EventKindServiceIF eventKindService, @NonNull EventKindTypeServiceIF eventKindTypeService) {
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

    Kind[] kindArray = eventKindTypeService.getKindArray();
    List<Kind> kindList = eventKindTypeService.getKinds();

    Kind eventMessageKind = eventMessage.getEvent().getKind();

    if (kindList.stream().noneMatch(eventMessageKind::equals)) {
      eventKindService.processIncomingEvent(eventMessage.getEvent());
      return;
    }

    KindTypeIF[] kindTypesArray = eventKindTypeService.getKindTypesArray();
    List<KindTypeIF> kindTypesList = eventKindTypeService.getKindTypes();

    eventKindTypeService.processIncomingKindTypeEvent((GenericEventKindTypeIF) eventMessage.getEvent());
  }
}
