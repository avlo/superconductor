package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.auth.AutoConfigReqMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.web.req.ReqApiAuthUi;
import com.prosilion.superconductor.base.controller.ReqApiUiIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityServiceIF;
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
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF) {
    log.debug("{} loading REDIS bean (REQ AUTH)", getClass().getSimpleName());
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authNosqlEntityServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthNosqlEntityServiceIF authNosqlEntityServiceIF(@NonNull AuthNosqlEntityRepository authNosqlEntityRepository) {
    return new AuthNosqlEntityService(authNosqlEntityRepository);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("{} loading REDIS AuthMessageServiceIF bean", getClass().getSimpleName());
    return new AuthMessageService<>(authNosqlEntityServiceIF, okResponseService, challengeRelayUrl);
  }
  
  @Bean
  @ConditionalOnMissingBean
  ReqApiUiIF reqApiUiIF() {
    return new ReqApiAuthUi();
  }
}
