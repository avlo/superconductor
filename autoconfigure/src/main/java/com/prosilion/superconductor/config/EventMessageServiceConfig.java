package com.prosilion.superconductor.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.event.EventServiceIF;
import com.prosilion.superconductor.service.message.event.EventMessageServiceBean;
import com.prosilion.superconductor.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.service.message.event.auth.EventMessageServiceAuthDecorator;
import com.prosilion.superconductor.service.message.event.auth.EventMessageServiceNoAuthDecorator;
import com.prosilion.superconductor.service.message.event.noop.EventMessageNoOpService;
import com.prosilion.superconductor.service.message.event.standard.EventMessageService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
//@Configuration
public class EventMessageServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "superconductor.noop.event", havingValue = "false", matchIfMissing = true)
  EventMessageServiceBean<EventMessage> getEventMessageService(
      @NonNull EventServiceIF<GenericEvent> eventService,
      @NonNull ClientResponseService clientResponseService) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new EventMessageService<>(eventService, clientResponseService);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "superconductor.noop.event", havingValue = "true")
  EventMessageServiceBean<EventMessage> getEventMessageNoOpService(
      @NonNull ClientResponseService clientResponseService,
      @NonNull @Value("${superconductor.noop.event.description}") String noOp) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new EventMessageNoOpService<>(clientResponseService, noOp);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "false", matchIfMissing = true)
  EventMessageServiceIF<EventMessage> getEventMessageServiceNoAuthDecorator(
      @NonNull EventMessageServiceBean<EventMessage> eventMessageService) {
    log.debug("loaded EventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new EventMessageServiceNoAuthDecorator<>(eventMessageService);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
  EventMessageServiceIF<EventMessage> getEventMessageServiceAuthDecorator(
      @NonNull EventMessageServiceBean<EventMessage> eventMessageService,
      @NonNull AuthEntityService authEntityService) {
    log.debug("loaded EventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new EventMessageServiceAuthDecorator<>(eventMessageService, authEntityService);
  }
}
