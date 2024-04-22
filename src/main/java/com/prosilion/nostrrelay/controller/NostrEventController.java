package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.service.message.CloseMessageService;
import com.prosilion.nostrrelay.service.message.EventMessageService;
import com.prosilion.nostrrelay.service.message.ReqMessageService;
import lombok.extern.java.Log;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.CloseMessage;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Log
@Controller
public class NostrEventController {

	private final ReqMessageService<ReqMessage> reqMessageService;
	private final EventMessageService<EventMessage> eventMessageService;
	private final CloseMessageService<CloseMessage> closeMessageService;
	private final Map<String, WebSocketSession> sessionHashMap = new HashMap<>();


	public NostrEventController(
			ReqMessageService<ReqMessage> reqMessageService,
			EventMessageService<EventMessage> eventMessageService,
			CloseMessageService<CloseMessage> closeMessageService) {
		this.reqMessageService = reqMessageService;
		this.eventMessageService = eventMessageService;
		this.closeMessageService = closeMessageService;
	}

	@Async
	@EventListener
	public void handleWebsocketConnectListener(SessionConnectEvent event) {
		System.out.println(String.format("NostrEventController registered SessionConnectEvent event: [%s]", event.getWebSocketSession().getId()));
		this.sessionHashMap.put(event.getWebSocketSession().getId(), event.getWebSocketSession());
	}

	@Async
	@EventListener
	public void handleWebsocketDisconnectListener(SessionDisconnectEvent event) {
		System.out.println(String.format("NostrEventController disconnect event: [%s]", event.getSessionId()));
		this.sessionHashMap.remove(event.getSessionId());
	}

	@MessageMapping("/")
	@SendTo("/")
	public void processIncomingEvent(SimpMessageHeaderAccessor simp, String baseMessage) {
		BaseMessage message = new BaseMessageDecoder(baseMessage).decode();
		switch (message.getCommand()) {
			case "REQ" -> {
				log.log(Level.INFO, "REQ decoded, contents: {0}", message);
				reqMessageService.processIncoming((ReqMessage) message, simp.getSessionId());
			}
			case "EVENT" -> {
				log.log(Level.INFO, "EVENT decoded, contents: {0}", message);
				eventMessageService.processIncoming((EventMessage) message);
			}
			case "CLOSE" -> {
				log.log(Level.INFO, "CLOSE decoded, contents: {0}", message);
				closeMessageService.processIncoming((CloseMessage) message);
			}
			default -> throw new AssertionError("Unknown command " + message.getCommand());

		}
	}



}
