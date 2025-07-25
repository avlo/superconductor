package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.EventServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.auth.AutoConfigEventMessageServiceNoAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.noop.EventMessageNoOpService;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.standard.EventMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
//@Configuration
public class EventMessageServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "superconductor.noop.event", havingValue = "false", matchIfMissing = true)
  EventMessageServiceIF getEventMessageService(
      @NonNull EventServiceIF eventService,
      @NonNull ClientResponseService clientResponseService) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new EventMessageService(eventService, clientResponseService);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty(name = "superconductor.noop.event", havingValue = "true")
  EventMessageServiceIF getEventMessageNoOpService(
      @NonNull ClientResponseService clientResponseService,
      @NonNull @Value("${superconductor.noop.event.description}") String noOp) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new EventMessageNoOpService(clientResponseService, noOp);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "false", matchIfMissing = true)
  AutoConfigEventMessageServiceIF getEventMessageServiceNoAuthDecorator(
      @NonNull EventMessageServiceIF eventMessageService) {
    log.debug("loaded AutoConfigEventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new AutoConfigEventMessageServiceNoAuthDecorator(eventMessageService);
  }

//  @Bean
//  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
//  AutoConfigEventMessageServiceIF<EventMessage> getEventMessageServiceAuthDecorator(
//      @NonNull EventMessageServiceIF<EventMessage> eventMessageService,
//      @NonNull AuthEntityService authEntityService) {
//    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT AUTH)");
//    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageService, authEntityService);
//  }
}
