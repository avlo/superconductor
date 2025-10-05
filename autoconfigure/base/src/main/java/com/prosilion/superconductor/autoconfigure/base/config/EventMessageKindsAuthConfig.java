package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.autoconfigure.base.EventKindsAuth;
import com.prosilion.superconductor.autoconfigure.base.EventKindsAuthCondition;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.AutoConfigEventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.EventMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.event.auth.AutoConfigEventMessageServiceAuthDecorator;
import com.prosilion.superconductor.autoconfigure.base.web.event.EventApiAuthUi;
import com.prosilion.superconductor.base.controller.EventApiUiIF;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuthIF;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.base.service.message.AuthMessageService;
import com.prosilion.superconductor.base.service.message.AuthMessageServiceIF;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@Conditional(EventKindsAuthCondition.class)
public class EventMessageKindsAuthConfig {
  @Bean
  @ConditionalOnMissingBean
  EventKindsAuthIF eventKindsAuthIF(@Value("#{'${superconductor.auth.event.kinds}'.split(',')}") List<String> eventAuthenticationKinds) {
    return new EventKindsAuth(eventAuthenticationKinds.stream().map(Kind::valueOf).toList());
  }

  @Bean
  @ConditionalOnMissingBean
  <T, U extends AuthPersistantIF> AutoConfigEventMessageServiceIF autoConfigEventMessageServiceIF(
      @NonNull EventMessageServiceIF eventMessageServiceIF,
      @NonNull AuthKindPersistantServiceIF<T, U> authKindNoSqlEntityServiceIF) {
    log.debug("{} loading REDIS AutoConfigEventMessageServiceAuthDecorator bean (EVENT+Kind AUTH)", getClass().getSimpleName());
    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageServiceIF, authKindNoSqlEntityServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  <T, U extends AuthPersistantIF> AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthPersistantServiceIF<T, U> authPersistantServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("{} loading JPA AuthMessageServiceIF bean", getClass().getSimpleName());
    return new AuthMessageService<>(authPersistantServiceIF, okResponseService, challengeRelayUrl);
  }

  @Bean
  @ConditionalOnMissingBean
  EventApiUiIF eventApiUiIF() {
    return new EventApiAuthUi();
  }
}
