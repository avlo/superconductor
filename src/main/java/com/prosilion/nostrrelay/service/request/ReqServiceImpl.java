package com.prosilion.nostrrelay.service.request;

import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.list.FiltersList;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
@Getter
public class ReqServiceImpl<T extends ReqMessage> implements ReqService<T> {
  private FiltersList filtersList;
  private String subId;

  public ReqServiceImpl(@NotNull T eventMessage) {
    filtersList = eventMessage.getFiltersList();
    subId = eventMessage.getSubscriptionId();
  }

  public T processIncoming() {
    //    TODO:
    //    log.log(Level.INFO, "processing BASE EVENT...", eventMessage.getEvent().toString());
    //    return new NIP01<>(Identity.getInstance()).createTextNoteEvent("******************* SERVER CONFIRMS PROCESSED, BASE *******************").getEvent();
    return null;
  }
}
