package com.prosilion.nostrrelay.service.message;

import com.prosilion.nostrrelay.service.event.ClassifiedListingEventService;
import com.prosilion.nostrrelay.service.event.TextNoteEventService;
import lombok.extern.java.Log;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
public class EventMessageService<T extends EventMessage> implements MessageService<T> {

	private final TextNoteEventService<T> textNoteEventService;
	private final ClassifiedListingEventService<T> classifiedListingEventService;

	@Autowired
	public EventMessageService(TextNoteEventService<T> textNoteEventService, ClassifiedListingEventService<T> classifiedListingEventService) {
		this.textNoteEventService = textNoteEventService;
		this.classifiedListingEventService = classifiedListingEventService;
	}

	public void processIncoming(@NotNull T eventMessage) {
		log.log(Level.INFO, "EVENT message NIP: {0}", eventMessage.getNip());
		// TOOD: update subscription logic
		eventMessage.setSubscriptionId("SUB ID");
		var kind = ((GenericEvent) eventMessage.getEvent()).getKind();
		log.log(Level.INFO, "EVENT message type: {0}", eventMessage.getEvent());
		createEventService(Kind.valueOf(kind), eventMessage);
//    return NIP01.createEventMessage(eventMessage.getEvent(), eventMessage.getSubscriptionId());
	}

	private void createEventService(@NotNull Kind kind, T eventMessage) {
		switch (kind) {
			case TEXT_NOTE -> {
				log.log(Level.INFO, "TEXT_NOTE KIND decoded should match TEXT_NOTE -> [{0}]", kind.getName());
				textNoteEventService.processIncoming(eventMessage);
			}
			case CLASSIFIED_LISTING -> {
				log.log(Level.INFO, "CLASSIFIED_LISTING KIND decoded should match CLASSIFIED_LISTING -> [{0}]", kind.getName());
				classifiedListingEventService.processIncoming(eventMessage);
			}

			default -> throw new AssertionError("Unknown kind: " + kind.getName());
		}
	}
}