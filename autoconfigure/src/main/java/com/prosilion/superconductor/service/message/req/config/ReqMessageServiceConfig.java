package com.prosilion.superconductor.service.message.req.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.message.req.ReqMessageService;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.service.message.req.AutoConfigReqMessageService;
import com.prosilion.superconductor.service.message.req.auth.AutoConfigReqMessageServiceAuthDecorator;
import com.prosilion.superconductor.service.message.req.auth.AutoConfigReqMessageServiceNoAuthDecorator;
import com.prosilion.superconductor.service.request.ReqService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.ReqMessage;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
//@Configuration
public class ReqMessageServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  ReqMessageServiceIF<ReqMessage> getReqMessageService(
      @NonNull ReqService<GenericEvent> reqService,
      @NonNull ClientResponseService clientResponseService) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new ReqMessageService<>(reqService, clientResponseService);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "false", matchIfMissing = true)
  AutoConfigReqMessageService<ReqMessage> getReqMessageServiceNoAuthDecorator(
      @NonNull ReqMessageServiceIF<ReqMessage> reqMessageServiceIF) {
    log.debug("loaded AutoConfigEventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new AutoConfigReqMessageServiceNoAuthDecorator<>(reqMessageServiceIF);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
  AutoConfigReqMessageService<ReqMessage> getReqMessageServiceAuthDecorator(
      @NonNull ReqMessageServiceIF<ReqMessage> reqMessageServiceIF,
      @NonNull AuthEntityService authEntityService) {
    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authEntityService);
  }
}
