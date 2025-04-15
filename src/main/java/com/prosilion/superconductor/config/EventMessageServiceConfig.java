package com.prosilion.superconductor.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.message.auth.EventMessageServiceAuthDecorator;
import com.prosilion.superconductor.service.message.auth.EventMessageServiceNoAuthDecorator;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "afterimage.noop.event", havingValue = "false")
public class EventMessageServiceConfig {

//  TODO: remove below comment once confirm proper to always load EventService as @Service
//  @Bean
//  EventServiceIF<GenericEvent> eventService(
//      NotifierService<GenericEvent> notifierService,
//      EventTypeServiceIF<GenericEvent> eventTypeService) {
//    log.debug("loaded EventService bean (EVENT)");
//    return new EventService<>(notifierService, eventTypeService);
//  }

  @Bean
  @ConditionalOnProperty(name = "afterimage.auth.active", havingValue = "false")
  MessageService<EventMessage> getEventMessageServiceNoAuthDecorator(
      EventServiceIF<GenericEvent> eventService,
      ClientResponseService okResponseService) {
    log.debug("loaded EventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new EventMessageServiceNoAuthDecorator<>(eventService, okResponseService);
  }

  @Bean
  @ConditionalOnProperty(name = "afterimage.auth.active", havingValue = "true")
  MessageService<EventMessage> getEventMessageServiceAuthDecorator(
      EventServiceIF<GenericEvent> eventService,
      ClientResponseService clientResponseService,
      AuthEntityService authEntityService) {
    log.debug("loaded EventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new EventMessageServiceAuthDecorator<>(eventService, clientResponseService, authEntityService);
  }
}
