package com.prosilion.superconductor.autoconfigure.redis.config;

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
import com.prosilion.superconductor.lib.redis.repository.auth.AuthDocumentRepository;
import com.prosilion.superconductor.lib.redis.service.auth.AuthDocumentService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthDocumentServiceIF;
import com.prosilion.superconductor.lib.redis.service.auth.AuthKindDocumentService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthKindDocumentServiceIF;
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
      @NonNull AuthKindDocumentServiceIF authKindDocumentServiceIF) {
    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageServiceIF, authKindDocumentServiceIF);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.req.active", havingValue = "true")
  @ConditionalOnMissingBean
  AutoConfigReqMessageServiceIF autoConfigReqMessageServiceIF(
      @NonNull ReqMessageServiceIF reqMessageServiceIF,
      @NonNull AuthDocumentServiceIF authDocumentServiceIF) {
    log.debug("loaded AutoConfigReqMessageServiceAuthDecorator bean (EVENT AUTH)");
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authDocumentServiceIF);
  }

  @Bean
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
  @ConditionalOnMissingBean
  AuthDocumentServiceIF authDocumentServiceIF(@NonNull AuthDocumentRepository authDocumentRepository) {
    return new AuthDocumentService(authDocumentRepository);
  }

  @Bean
  @ConditionalOnBean(AuthEventKinds.class)
  @ConditionalOnMissingBean
  AuthKindDocumentServiceIF authKindDocumentServiceIF(
      @NonNull AuthDocumentServiceIF authDocumentServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    return new AuthKindDocumentService(authDocumentServiceIF, authEventKinds);
  }

  @Bean
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
  @ConditionalOnMissingBean
  AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthDocumentServiceIF authDocumentServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("loaded REDIS AuthMessageServiceIF bean");
    return new AuthMessageService<>(authDocumentServiceIF, okResponseService, challengeRelayUrl);
  }
}
