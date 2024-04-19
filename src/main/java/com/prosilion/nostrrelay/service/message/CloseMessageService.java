package com.prosilion.nostrrelay.service.message;

import lombok.extern.java.Log;
import nostr.event.message.CloseMessage;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Log
@Service
public class CloseMessageService<T extends CloseMessage> implements MessageService<T> {

	@Override
	public void processIncoming(T closeMessage) {
		log.log(Level.INFO, "processing CLOSE event");
		new CloseMessage(closeMessage.getSubscriptionId());
	}
}
