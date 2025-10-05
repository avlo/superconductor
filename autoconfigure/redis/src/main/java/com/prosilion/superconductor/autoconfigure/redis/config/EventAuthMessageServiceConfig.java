package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.base.service.event.auth.EventKindsAuth;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuthCondition;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.auth.AuthKindNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@Conditional(EventKindsAuthCondition.class)
public class EventAuthMessageServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  AuthNosqlEntityServiceIF authPersistantServiceIF(@NonNull AuthNosqlEntityRepository authNosqlEntityRepository) {
    return new AuthNosqlEntityService(authNosqlEntityRepository);
  }

  @Bean
  @ConditionalOnBean(EventKindsAuth.class)
  @ConditionalOnMissingBean
  AuthKindPersistantServiceIF<AuthNosqlEntityIF, AuthNosqlEntityIF> authKindNosqlEntityServiceIF(
      @NonNull AuthNosqlEntityServiceIF authPersistantServiceIF,
      @NonNull EventKindsAuth eventKindsAuth) {
    return new AuthKindNosqlEntityService(authPersistantServiceIF, eventKindsAuth);
  }
}
