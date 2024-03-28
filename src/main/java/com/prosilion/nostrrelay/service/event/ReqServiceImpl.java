package com.prosilion.nostrrelay.service.event;

import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
@Getter
public class ReqServiceImpl<T extends ReqMessage> implements ReqService<T> {
  private final T eventMessage;

  public ReqServiceImpl(@NotNull T eventMessage) {
    this.eventMessage = eventMessage;
    log.log(Level.INFO, "EventService Constructed");
    //    log.log(Level.INFO, "EVENT message KIND: {0}", ((GenericEvent) eventMessage.getEvent()).getKind());
    log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
    //    log.log(Level.INFO, "EVENT message JSON: {0}", new Gson().toJson(eventMessage.getEvent().toString()));
  }

  public T processIncoming() {
    //    TODO:
    //    log.log(Level.INFO, "processing BASE EVENT...", eventMessage.getEvent().toString());
    //    return new NIP01<>(Identity.getInstance()).createTextNoteEvent("******************* SERVER CONFIRMS PROCESSED, BASE *******************").getEvent();
    return null;
  }
}
