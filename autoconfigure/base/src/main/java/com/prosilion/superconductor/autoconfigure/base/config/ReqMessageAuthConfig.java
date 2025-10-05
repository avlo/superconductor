package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.auth.AutoConfigReqMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.web.req.ReqApiAuthUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import com.prosilion.superconductor.base.service.event.auth.ReqAuthCondition;
import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@Conditional(ReqAuthCondition.class)
public class ReqMessageAuthConfig {
  @Bean
  @ConditionalOnMissingBean
  <T, U extends AuthPersistantIF> AutoConfigReqMessageServiceIF autoConfigReqMessageServiceIF(
      @NonNull ReqMessageServiceIF reqMessageServiceIF,
      @NonNull AuthPersistantServiceIF<T, U> authPersistantServiceIF) {
    log.debug("{} loading JPA bean (REQ AUTH)", getClass().getSimpleName());
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authPersistantServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  <T, U extends AuthPersistantIF> AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthPersistantServiceIF<T, U> authPersistantServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("{} loading REDIS AuthMessageServiceIF bean", getClass().getSimpleName());
    return new AuthMessageService<>(authPersistantServiceIF, okResponseService, challengeRelayUrl);
  }

  @Bean
  @ConditionalOnMissingBean
  ReqApiUiIF reqApiUiIF() {
    return new ReqApiAuthUi();
  }
}
