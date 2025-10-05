package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.autoconfigure.base.EventKindsAuthCondition;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuthIF;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthKindJpaEntityService;
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
  AuthKindPersistantServiceIF<Long, AuthJpaEntityIF> authKindJpaEntityServiceIF(
      @NonNull AuthJpaEntityRepository authJpaEntityRepository,
      @NonNull EventKindsAuthIF eventKindsAuthIF) {
    return new AuthKindJpaEntityService(
        new AuthJpaEntityService(authJpaEntityRepository),
        eventKindsAuthIF);
  }
}
