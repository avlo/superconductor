package com.prosilion.superconductor.service.message.req.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.message.req.ReqMessageService;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceBean;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.service.message.req.auth.ReqMessageServiceAuthDecorator;
import com.prosilion.superconductor.service.message.req.auth.ReqMessageServiceNoAuthDecorator;
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
  ReqMessageServiceBean<ReqMessage> getReqMessageService(
      @NonNull ReqService<GenericEvent> reqService,
      @NonNull ClientResponseService clientResponseService) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new ReqMessageService<>(reqService, clientResponseService);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "false", matchIfMissing = true)
  ReqMessageServiceIF<ReqMessage> getReqMessageServiceNoAuthDecorator(
      @NonNull ReqMessageServiceBean<ReqMessage> reqMessageServiceBean) {
    log.debug("loaded EventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new ReqMessageServiceNoAuthDecorator<>(reqMessageServiceBean);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
  ReqMessageServiceIF<ReqMessage> getReqMessageServiceAuthDecorator(
      @NonNull ReqMessageServiceBean<ReqMessage> reqMessageServiceBean,
      @NonNull AuthEntityService authEntityService) {
    log.debug("loaded EventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new ReqMessageServiceAuthDecorator<>(reqMessageServiceBean, authEntityService);
  }
}
