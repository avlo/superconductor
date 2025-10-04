package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.auth.AutoConfigReqMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.web.req.ReqApiAuthUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(name = "superconductor.auth.req.active", havingValue = "true")
public class ReqAuthMessageServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  AutoConfigReqMessageServiceIF autoConfigReqMessageServiceIF(
      @NonNull ReqMessageServiceIF reqMessageServiceIF,
      @NonNull AuthJpaEntityServiceIF authJpaEntityServiceIF) {
    log.debug("{} loading JPA bean (REQ AUTH)", getClass().getSimpleName());
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authJpaEntityServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthJpaEntityServiceIF authJpaEntityServiceIF(@NonNull AuthJpaEntityRepository authJpaEntityRepository) {
    return new AuthJpaEntityService(authJpaEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthJpaEntityServiceIF authJpaEntityServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("{} loading JPA AuthMessageServiceIF bean", getClass().getSimpleName());
    return new AuthMessageService<>(authJpaEntityServiceIF, okResponseService, challengeRelayUrl);
  }

  @Bean
  @ConditionalOnMissingBean
  ReqApiUiIF reqApiUiIF() {
    return new ReqApiAuthUi();
  }
}
