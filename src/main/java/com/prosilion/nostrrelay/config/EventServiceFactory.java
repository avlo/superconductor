package com.prosilion.nostrrelay.config;

import com.prosilion.nostrrelay.service.EventService;
import com.prosilion.nostrrelay.service.EventServiceImpl;
import com.prosilion.nostrrelay.service.TextNoteEventServiceImpl;
import lombok.experimental.UtilityClass;
import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

@Log
@UtilityClass
public class EventServiceFactory {

//  TODO: below suggested by IDE, no idea what it does but might be useful.  revisit
//  @Contract("_, _ -> new")
  public static @NotNull EventService<EventMessage> createEventService(@NotNull Kind kind, EventMessage eventMessage) {
    switch (kind) {
      case SET_METADATA -> {
        log.log(Level.INFO, "SET_METADATA KIND decoded should match SET_METADATA -> [{0}]", kind.getName());
        return new EventServiceImpl<>(eventMessage);
      }
      case TEXT_NOTE -> {
        log.log(Level.INFO, "TEXT_NOTE KIND decoded should match TEXT_NOTE -> [{0}]", kind.getName());
        return new TextNoteEventServiceImpl(eventMessage);
      }
      default -> throw new AssertionError("Unknown kind: " + kind.getName());
    }
  }

}
