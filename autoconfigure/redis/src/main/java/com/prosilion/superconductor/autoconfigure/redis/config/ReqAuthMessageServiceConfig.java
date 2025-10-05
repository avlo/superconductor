package com.prosilion.superconductor.autoconfigure.redis.config;

import com.prosilion.superconductor.base.service.event.auth.ReqAuthCondition;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthNosqlEntityRepository;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityService;
import com.prosilion.superconductor.lib.redis.service.auth.AuthNosqlEntityServiceIF;
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
  AuthNosqlEntityServiceIF authNosqlEntityServiceIF(@NonNull AuthNosqlEntityRepository authNosqlEntityRepository) {
    return new AuthNosqlEntityService(authNosqlEntityRepository);
  }
}
