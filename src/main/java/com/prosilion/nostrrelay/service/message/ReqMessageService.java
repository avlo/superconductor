package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.service.request.ReqService;
import com.prosilion.nostrrelay.service.request.ReqServiceImpl;
import jakarta.websocket.Session;
import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
public class ReqMessageService<T extends ReqMessage> implements MessageService<ReqMessage> {
  private final ReqService<T> reqService;
  private final T reqMessage;

  public ReqMessageService(@NotNull T reqMessage) {
    log.log(Level.INFO, "REQ fitlers: {0}", reqMessage.getFiltersList());
    this.reqMessage = reqMessage;
    reqService = new ReqServiceImpl<>(reqMessage);
  }

  @Override
  public ReqMessage processIncoming(Session session) {
    return reqService.processIncoming(session);
  }

  private ReqService<T> createEventService(@NotNull Kind kind, T reqMessage) {
    //    private @NotNull ReqService<T> createEventService(@NotNull Kind kind, T reqMessage) {
    //    TODO:
    //    switch (kind) {
    //      case SET_METADATA -> {
    //        log.log(Level.INFO, "SET_METADATA KIND decoded should match SET_METADATA -> [{0}]", kind.getName());
    //        return new EventServiceImpl<>(reqMessage);
    //      }
    //      case TEXT_NOTE -> {
    //        log.log(Level.INFO, "TEXT_NOTE KIND decoded should match TEXT_NOTE -> [{0}]", kind.getName());
    //        return new TextNoteEventServiceImpl<>(reqMessage);
    //      }
    //      case CLASSIFIED_LISTING -> {
    //        log.log(Level.INFO, "CLASSIFIED_LISTING KIND decoded should match CLASSIFIED_LISTING -> [{0}]", kind.getName());
    //        return new ClassifiedEventServiceImpl<>(reqMessage);
    //      }
    //
    //      default -> throw new AssertionError("Unknown kind: " + kind.getName());
    //    }
    return null;
  }
}