package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.auth.AutoConfigEventMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.web.event.EventApiAuthUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKinds;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKindsCondition;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.auth.AuthKindNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthKindNosqlEntityServiceIF;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@Conditional(AuthEventKindsCondition.class)
public class EventAuthMessageServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  AutoConfigEventMessageServiceIF autoConfigEventMessageServiceIF(
      @NonNull EventMessageServiceIF eventMessageServiceIF,
      @NonNull AuthKindNosqlEntityServiceIF authKindNoSqlEntityServiceIF) {
    log.debug("{} loading REDIS AutoConfigEventMessageServiceAuthDecorator bean (EVENT+Kind AUTH)", getClass().getSimpleName());
    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageServiceIF, authKindNoSqlEntityServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthNosqlEntityServiceIF authNosqlEntityServiceIF(@NonNull AuthNosqlEntityRepository authNosqlEntityRepository) {
    return new AuthNosqlEntityService(authNosqlEntityRepository);
  }

  @Bean
  @ConditionalOnBean(AuthEventKinds.class)
  @ConditionalOnMissingBean
  AuthKindNosqlEntityServiceIF authKindNosqlEntityServiceIF(
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    return new AuthKindNosqlEntityService(authNosqlEntityServiceIF, authEventKinds);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("{} loading JPA AuthMessageServiceIF bean", getClass().getSimpleName());
    return new AuthMessageService<>(authNosqlEntityServiceIF, okResponseService, challengeRelayUrl);
  }

  @Bean
  @ConditionalOnMissingBean
  EventApiUiIF eventApiUiIF() {
    return new EventApiAuthUi();
  }
}
