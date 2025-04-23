package com.prosilion.superconductor.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.event.EventMessageServiceIF;
import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.auth.EventMessageServiceAuthDecorator;
import com.prosilion.superconductor.service.auth.EventMessageServiceNoAuthDecorator;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "superconductor.noop.event", havingValue = "false", matchIfMissing = true)
public class EventMessageServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "false", matchIfMissing = true)
  EventMessageServiceIF<EventMessage> getEventMessageServiceNoAuthDecorator(
      EventServiceIF<GenericEvent> eventService,
      ClientResponseService okResponseService) {
    log.debug("loaded EventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new EventMessageServiceNoAuthDecorator<>(eventService, okResponseService);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
  EventMessageServiceIF<EventMessage> getEventMessageServiceAuthDecorator(
      EventServiceIF<GenericEvent> eventService,
      ClientResponseService clientResponseService,
      AuthEntityService authEntityService) {
    log.debug("loaded EventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new EventMessageServiceAuthDecorator<>(eventService, clientResponseService, authEntityService);
  }
}
