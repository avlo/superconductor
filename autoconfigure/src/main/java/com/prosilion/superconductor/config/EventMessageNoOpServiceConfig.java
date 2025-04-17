package com.prosilion.superconductor.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.event.noop.EventMessageNoOpService;
import lombok.extern.slf4j.Slf4j;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "superconductor.noop.event", havingValue = "true")
public class EventMessageNoOpServiceConfig {
  @Bean
  MessageService<EventMessage> getEventMessageService(
      ClientResponseService clientResponseService,
      @Value("${superconductor.noop.event.description}") String noOp) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new EventMessageNoOpService<>(clientResponseService, noOp);
  }
}
