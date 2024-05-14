package com.prosilion.superconductor.service.okresponse;

import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientOkResponseService<T extends EventMessage> {
  private final ApplicationEventPublisher publisher;
  public ClientOkResponseService(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void processOkClientResponse(String sessionId, T eventMessage) {
    log.info("Processing event message: {}", eventMessage.getEvent());
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    publisher.publishEvent(new OkClientResponse(sessionId, event));
  }
}
