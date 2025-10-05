package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.autoconfigure.base.EventKindsAuthCondition;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuthIF;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.auth.AuthKindNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityService;
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
  @ConditionalOnBean(EventKindsAuthIF.class)
  @ConditionalOnMissingBean
  AuthKindPersistantServiceIF<AuthNosqlEntityIF, AuthNosqlEntityIF> authKindNosqlEntityServiceIF(
      @NonNull AuthNosqlEntityRepository authNosqlEntityRepository,
      @NonNull EventKindsAuthIF eventKindsAuthIF) {
    return new AuthKindNosqlEntityService(
        new AuthNosqlEntityService(authNosqlEntityRepository),
        eventKindsAuthIF);
  }
}
