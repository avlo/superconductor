package com.prosilion.nostrrelay.service.request;

import com.prosilion.nostrrelay.entity.Subscriber;
import lombok.Getter;
import lombok.extern.java.Log;
import nostr.event.list.FiltersList;
import nostr.event.message.ReqMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Log
@Getter
@Service
public class ReqService<T extends ReqMessage> implements ReqServiceIF<T> {
	private final SubscriberService subscriberService;

	public ReqService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public void processIncoming(@NotNull T reqMessage, String sessionId) {
		FiltersList filtersList = reqMessage.getFiltersList();
		String subId = reqMessage.getSubscriptionId();
		Subscriber subscriber = new Subscriber(subId, sessionId);
		subscriberService.save(subscriber, filtersList);
	}
}
