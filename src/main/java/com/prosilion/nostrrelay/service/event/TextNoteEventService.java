package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.entity.Subscriber;
import com.prosilion.nostrrelay.pubsub.BroadcastMessageEvent;
import com.prosilion.nostrrelay.pubsub.FireNostrEvent;
import com.prosilion.nostrrelay.service.request.SubscriberService;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Log
@Service
public class TextNoteEventService<T extends EventMessage> implements EventServiceIF<T> {
	private final SubscriberService subscriberService;
	private final EventService<TextNoteEvent> eventService;

	public TextNoteEventService(EventService<TextNoteEvent> eventService, SubscriberService subscriberService) {
		this.eventService = eventService;
		this.subscriberService = subscriberService;
	}

	@Override
	public void processIncoming(T eventMessage) {
		GenericEvent event = (GenericEvent) eventMessage.getEvent();
		event.setNip(1);
		event.setKind(Kind.TEXT_NOTE.getValue());
		TextNoteEvent textNoteEvent = new TextNoteEvent(
				event.getPubKey(),
				event.getTags(),
				event.getContent()
		);
		Long id = eventService.saveEventEntity(event);
		textNoteEvent.setId(event.getId());
		eventService.publishEvent(id, textNoteEvent);
	}

	@EventListener
	public void handleFireNostrEvent(FireNostrEvent textNoteEvent) {
		// TODO: value of?
		EventMessage message = NIP01.createEventMessage(textNoteEvent.event(), String.valueOf(textNoteEvent.subscriberId()));
		Subscriber subscriber = subscriberService.get(textNoteEvent.subscriberId());
		BroadcastMessageEvent<EventMessage> event = new BroadcastMessageEvent<>(subscriber.getSessionId(), message);
		eventService.publishEvent(event);
	}
}
