package com.prosilion.nostrrelay.service.event;

import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.base.IEvent;
import nostr.event.BaseTag;
import nostr.event.message.EventMessage;
import nostr.event.tag.EventTag;
import nostr.id.Identity;

import java.util.List;
import java.util.logging.Level;

@Log
@Getter
public class TextNoteEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {
  public TextNoteEventServiceImpl(T eventMessage) {
    super(eventMessage);
  }

  @Override
  public IEvent processIncoming() {
    log.log(Level.INFO, "processing incoming TEXT_NOTE_EVENT: [{0}]", getEventMessage());
    List<BaseTag> tags = List.of(new EventTag(getEventMessage().getEvent().getId()));
    var originalContent = getEventMessage().toString();
    return new NIP01<>(Identity.getInstance()).createTextNoteEvent(tags, originalContent).getEvent();
  }
}
