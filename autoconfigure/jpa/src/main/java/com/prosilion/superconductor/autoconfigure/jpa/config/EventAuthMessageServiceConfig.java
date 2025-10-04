package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.auth.AutoConfigEventMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.web.event.EventApiAuthUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKinds;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityServiceIF;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthKindJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthKindJpaEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty()}")
public class EventAuthMessageServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  AutoConfigEventMessageServiceIF autoConfigEventMessageServiceIF(
      @NonNull EventMessageServiceIF eventMessageServiceIF,
      @NonNull AuthKindJpaEntityServiceIF authKindJpaEntityServiceIF) {
    log.debug("{} loading JPA AutoConfigEventMessageServiceAuthDecorator bean (EVENT+Kind AUTH)", getClass().getSimpleName());
    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageServiceIF, authKindJpaEntityServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  AuthJpaEntityServiceIF authJpaEntityServiceIF(@NonNull AuthJpaEntityRepository authJpaEntityRepository) {
    return new AuthJpaEntityService(authJpaEntityRepository);
  }

  @Bean
  @ConditionalOnBean(AuthEventKinds.class)
  @ConditionalOnMissingBean
  AuthKindJpaEntityServiceIF authKindJpaEntityServiceIF(
      @NonNull AuthJpaEntityServiceIF authJpaEntityServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    return new AuthKindJpaEntityService(authJpaEntityServiceIF, authEventKinds);
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
  EventApiUiIF eventApiUiIF() {
    return new EventApiAuthUi();
  }
}
