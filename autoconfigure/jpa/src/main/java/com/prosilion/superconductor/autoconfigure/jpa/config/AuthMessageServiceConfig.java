package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.auth.AutoConfigEventMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.auth.AutoConfigReqMessageServiceAuthDecorator;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKinds;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthEntityServiceIF;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthKindEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthKindEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class AuthMessageServiceConfig {
  @Bean
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty()}")
  @ConditionalOnMissingBean
  AutoConfigEventMessageServiceIF autoConfigEventMessageServiceIF(
      @NonNull EventMessageServiceIF eventMessageServiceIF,
      @NonNull AuthKindEntityServiceIF authKindEntityServiceIF) {
    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageServiceIF, authKindEntityServiceIF);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.req.active", havingValue = "true")
  @ConditionalOnMissingBean
  AutoConfigReqMessageServiceIF autoConfigReqMessageServiceIF(
      @NonNull ReqMessageServiceIF reqMessageServiceIF,
      @NonNull AuthEntityServiceIF authEntityServiceIF) {
    log.debug("loaded AutoConfigReqMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authEntityServiceIF);
  }

  @Bean
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
  @ConditionalOnMissingBean
  AuthEntityServiceIF authEntityServiceIF(@NonNull AuthEntityRepository authEntityRepository) {
    return new AuthEntityService(authEntityRepository);
  }

  @Bean
  @ConditionalOnBean(AuthEventKinds.class)
  @ConditionalOnMissingBean
  AuthKindEntityServiceIF authKindEntityServiceIF(
      @NonNull AuthEntityServiceIF authEntityServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    return new AuthKindEntityService(authEntityServiceIF, authEventKinds);
  }

  @Bean
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
  @ConditionalOnMissingBean
  AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthEntityServiceIF authEntityServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("loaded JPA AuthMessageServiceIF bean");
    return new AuthMessageService<>(authEntityServiceIF, okResponseService, challengeRelayUrl);
  }
}
