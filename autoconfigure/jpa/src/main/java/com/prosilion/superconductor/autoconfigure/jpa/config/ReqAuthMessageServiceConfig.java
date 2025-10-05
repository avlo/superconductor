package com.prosilion.superconductor.autoconfigure.jpa.config;

import com.prosilion.superconductor.base.service.event.auth.ReqAuthCondition;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityService;
import com.prosilion.superconductor.lib.jpa.service.auth.AuthJpaEntityServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@Conditional(ReqAuthCondition.class)
public class ReqAuthMessageServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  AuthJpaEntityServiceIF authJpaEntityServiceIF(@NonNull AuthJpaEntityRepository authJpaEntityRepository) {
    return new AuthJpaEntityService(authJpaEntityRepository);
  }
}
