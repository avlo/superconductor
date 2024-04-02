package com.prosilion.nostrrelay.service.event.textnote;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import com.prosilion.nostrrelay.service.event.join.EventTagEntityServiceImpl;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.base.IEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import nostr.id.Identity;

import java.lang.reflect.InvocationTargetException;

@Log
@Getter
public class TextNoteEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {
  private final EventTagEntityServiceImpl eventTagEntityService;

  public TextNoteEventServiceImpl(T eventMessage) {
    super(eventMessage);
    eventTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityServiceImpl.class);
  }

  @Override
  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    NIP01<TextNoteEvent> nip01EventNIP01 = new NIP01<>(Identity.getInstance(event.getPubKey().toString()));
    NIP01<TextNoteEvent> textNoteEvent = nip01EventNIP01.createTextNoteEvent(event.getTags(), event.getContent());
    eventTagEntityService.save(textNoteEvent.getEvent(), getEventMessage().getEvent().getId());
    return new NIP01<>(Identity.getInstance()).createTextNoteEvent(event.getTags(), getEventMessage().toString()).getEvent();
  }
}
