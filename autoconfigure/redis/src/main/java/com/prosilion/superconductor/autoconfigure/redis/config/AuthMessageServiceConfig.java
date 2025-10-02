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
      @NonNull AuthKindNosqlEntityServiceIF authKindNoSqlEntityServiceIF) {
    log.debug("loaded AutoConfigEventMessageServiceAuthDecorator bean (EVENT+Kind AUTH)");
    return new AutoConfigEventMessageServiceAuthDecorator<>(eventMessageServiceIF, authKindNoSqlEntityServiceIF);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.req.active", havingValue = "true")
  @ConditionalOnMissingBean
  AutoConfigReqMessageServiceIF autoConfigReqMessageServiceIF(
      @NonNull ReqMessageServiceIF reqMessageServiceIF,
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF) {
    log.debug("loaded AutoConfigReqMessageServiceAuthDecorator bean (REQ AUTH)");
    return new AutoConfigReqMessageServiceAuthDecorator<>(reqMessageServiceIF, authNosqlEntityServiceIF);
  }

  @Bean
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
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
  @ConditionalOnExpression("#{!'${superconductor.auth.event.kinds}'.isEmpty() || ${superconductor.auth.req.active:true}}")
  @ConditionalOnMissingBean
  AuthMessageServiceIF authMessageServiceIF(
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.auth.challenge-relay.url}") String challengeRelayUrl) {
    log.debug("loaded REDIS AuthMessageServiceIF bean");
    return new AuthMessageService<>(authNosqlEntityServiceIF, okResponseService, challengeRelayUrl);
  }
}
