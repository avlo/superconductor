//package com.prosilion.superconductor.service.message.event.auth;
//
//import com.prosilion.superconductor.service.event.AuthEntityService;
//import com.prosilion.superconductor.service.message.event.AutoConfigEventMessageServiceIF;
//import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
//import java.util.NoSuchElementException;
//import org.springframework.lang.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import com.prosilion.nostr.message.EventMessage;
//
//@Slf4j
//public class AutoConfigEventMessageServiceAuthDecorator<T extends EventMessage> implements AutoConfigEventMessageServiceIF<T> {
//  public final String command = "EVENT";
//
//  private final EventMessageServiceIF<T> eventMessageServiceIF;
//  private final AuthEntityService authEntityService;
//
//  public AutoConfigEventMessageServiceAuthDecorator(
//      @NonNull EventMessageServiceIF<T> eventMessageServiceIF,
//      @NonNull AuthEntityService authEntityService) {
//    this.eventMessageServiceIF = eventMessageServiceIF;
//    this.authEntityService = authEntityService;
//  }
//
//  public void processIncoming(@NonNull T eventMessage, @NonNull String sessionId) {
//    log.debug("AUTHENTICATED EVENT message NIP: {}", eventMessage.getNip());
//    log.debug("AUTHENTICATED EVENT message type: {}", eventMessage.getEvent());
//    try {
//      authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
//    } catch (NoSuchElementException e) {
//      log.debug("AUTHENTICATED EVENT message failed session authentication");
//      processNotOkClientResponse(eventMessage, sessionId, String.format("restricted: session [%s] has not been authenticated", sessionId));
//      return;
//    }
//    eventMessageServiceIF.processIncoming(eventMessage, sessionId);
//  }
//
//  @Override
//  public String getCommand() {
//    return command;
//  }
//
//  @Override
//  public void processOkClientResponse(T eventMessage, @NonNull String sessionId) {
//    eventMessageServiceIF.processOkClientResponse(eventMessage, sessionId);
//  }
//
//  @Override
//  public void processNotOkClientResponse(T eventMessage, @NonNull String sessionId, @NonNull String errorMessage) {
//    eventMessageServiceIF.processNotOkClientResponse(eventMessage, sessionId, errorMessage);
//  }
//}
