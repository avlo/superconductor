//package com.prosilion.superconductor.service.message.event.standard;
//
//import com.prosilion.nostr.enums.Type;
//import com.prosilion.nostr.event.AbstractBadgeAwardEvent;
//import com.prosilion.nostr.event.GenericEventKindIF;
//import com.prosilion.nostr.message.EventMessage;
//import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
//import com.prosilion.superconductor.service.event.service.EventKindServiceIF;
//import com.prosilion.superconductor.service.event.service.EventKindTypeServiceIF;
//import com.prosilion.superconductor.service.event.service.plugin.AbstractEventKindTypePluginIF;
//import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
//import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//
//@Slf4j
//public class EventKindTypeMessageService<T extends EventMessage> implements EventMessageServiceIF<T> {
//  private final EventKindTypeServiceIF<Type, EventKindTypePluginIF<Type>> eventKindTypeService;
//  private final ClientResponseService clientResponseService;
//
//  public EventKindTypeMessageService(@NonNull EventKindTypeServiceIF<Type, EventKindTypePluginIF<Type>> eventKindTypeService, @NonNull ClientResponseService clientResponseService) {
//    this.eventKindTypeService = eventKindTypeService;
//    this.clientResponseService = clientResponseService;
//  }
//
//  @Override
//  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
//    eventKindTypeService.processIncomingEventKindType(eventMessage);
//    processOkClientResponse(eventMessage, sessionId);
//  }
//
//  public void processOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId) {
//    clientResponseService.processOkClientResponse(sessionId, eventMessage);
//  }
//
//  public void processNotOkClientResponse(@NonNull T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
//    clientResponseService.processNotOkClientResponse(sessionId, new EventMessage(eventMessage.getEvent()), errorMessage);
//  }
//}
