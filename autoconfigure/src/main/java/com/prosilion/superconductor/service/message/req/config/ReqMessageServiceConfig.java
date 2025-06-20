package com.prosilion.superconductor.service.message.req.config;

import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.service.message.req.ReqMessageService;
import com.prosilion.superconductor.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.service.message.req.auth.AutoConfigReqMessageServiceNoAuthDecorator;
import com.prosilion.superconductor.service.request.ReqServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class ReqMessageServiceConfig {

  @Bean
  ReqMessageServiceIF reqMessageService(
      @NonNull ReqServiceIF reqService,
      @NonNull ClientResponseService clientResponseService) {
    log.debug("loaded EventMessageNoOpService bean (NO_OP_EVENT)");
    return new ReqMessageService(reqService, clientResponseService);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "false", matchIfMissing = true)
  AutoConfigReqMessageServiceIF autoConfigReqMessageServiceNoAuthDecorator(
      @NonNull ReqMessageServiceIF reqMessageServiceIF) {
    log.debug("loaded AutoConfigEventMessageServiceNoAuthDecorator bean (EVENT NO-AUTH)");
    return new AutoConfigReqMessageServiceNoAuthDecorator(reqMessageServiceIF);
  }

//  @Bean
//  @ConditionalOnProperty(name = "superconductor.auth.active", havingValue = "true")
//  AutoConfigReqMessageServiceIF<T> autoConfigReqMessageServiceAuthDecorator(
//      @NonNull ReqMessageServiceIF<T> reqMessageServiceIF,
//      @NonNull AuthEntityService authEntityService) {
//    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT AUTH)");
//    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authEntityService);
//  }
}
