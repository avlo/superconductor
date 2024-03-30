package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import com.prosilion.nostrrelay.repository.EventRepository;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.base.IEvent;
import nostr.event.BaseTag;
import nostr.event.message.EventMessage;
import nostr.event.tag.EventTag;
import nostr.id.IIdentity;
import nostr.id.Identity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Log
@Getter
public class TextNoteEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {

  private final EventRepository eventRepository;

  public TextNoteEventServiceImpl(T eventMessage) {
    super(eventMessage);
    eventRepository = ApplicationContextProvider.getApplicationContext().getBean(EventRepository.class);
  }

  @Override
  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    //    log.log(Level.INFO, "processing incoming TEXT_NOTE_EVENT: [{0}]", getEventMessage());
    //    List<BaseTag> tags = List.of(new EventTag(getEventMessage().getEvent().getId()));
    //    var originalContent = getEventMessage().toString();
    //
    //    NIP01<NIP01Event> nip01EventNIP01 = new NIP01<>(Identity.getInstance());
    //    NIP01<NIP01Event> textNoteEvent = nip01EventNIP01.createTextNoteEvent(tags, originalContent);
    //    NIP01Event e = textNoteEvent.getEvent();


    // BELOW FUNCTIONAL POC
    //    List<BaseTag> tags = List.of(
    //        GenericTag.create("e", 1, "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"),
    //        GenericTag.create("p", 1, "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")
    //    );
    List<BaseTag> tags = List.of(new EventTag(getEventMessage().getEvent().getId()));

    IIdentity sender = Identity.getInstance();
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(sender.getPublicKey(), tags, getEventMessage().toString());
    textNoteEventDto.setId(getEventMessage().getEvent().getId());

    eventRepository.save(textNoteEventDto.convertDtoToEntity());

    return new NIP01<>(Identity.getInstance()).createTextNoteEvent(tags, "CONTENT").getEvent();
  }
}
