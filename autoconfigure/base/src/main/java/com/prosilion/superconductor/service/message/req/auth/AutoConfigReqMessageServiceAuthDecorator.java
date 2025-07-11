//package com.prosilion.superconductor.service.message.req.auth;
//
//import com.prosilion.superconductor.service.event.AuthEntityService;
//import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
//import com.prosilion.superconductor.service.message.req.AutoConfigReqMessageServiceIF;
//import java.util.NoSuchElementException;
//import org.springframework.lang.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import com.prosilion.nostr.message.ReqMessage;
//
//@Slf4j
//public class AutoConfigReqMessageServiceAuthDecorator<T extends ReqMessage> implements AutoConfigReqMessageServiceIF<T> {
//  public final String command = "REQ";
//  private final ReqMessageServiceIF<T> reqMessageService;
//  private final AuthEntityService authEntityService;
//
//  public AutoConfigReqMessageServiceAuthDecorator(
//      @NonNull ReqMessageServiceIF<T> reqMessageService,
//      @NonNull AuthEntityService authEntityService) {
//    this.reqMessageService = reqMessageService;
//    this.authEntityService = authEntityService;
//  }
//
//  @Override
//  public void processIncoming(@NonNull T reqMessage, @NonNull String sessionId) {
//    log.debug("AUTH REQ decoded, contents: {}", reqMessage);
//
//    try {
//      authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
//    } catch (NoSuchElementException e) {
//      log.debug("AUTHENTICATED REQ message failed session authentication");
//      reqMessageService.processNoticeClientResponse(reqMessage, sessionId, String.format("restricted: session [%s] has not been authenticated", sessionId));
//      return;
//    }
//    reqMessageService.processIncoming(reqMessage, sessionId);
//  }
//
//  @Override
//  public String getCommand() {
//    return command;
//  }
//}
