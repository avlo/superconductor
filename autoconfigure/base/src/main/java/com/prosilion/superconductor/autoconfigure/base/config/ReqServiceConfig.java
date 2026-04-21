package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.superconductor.base.service.request.ReqService;
import com.prosilion.superconductor.base.service.request.ReqServiceIF;
import com.prosilion.superconductor.base.service.request.subscriber.AbstractSubscriberService;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class ReqServiceConfig {
  @Bean
  @ConditionalOnMissingBean
  Duration requestTimeoutDuration(@NonNull @Value("${superconductor.timeout.duration.millis}") Long durationMillis) {
    return Duration.ofMillis(durationMillis);
  }

//
//  @Bean
//  @ConditionalOnMissingBean
//  NostrRelayReqConsolidatorService nostrRelayReqConsolidatorService(Duration requestTimeoutDuration) {
//    return new NostrRelayReqConsolidatorService(requestTimeoutDuration);
//  }

  @Bean
  ReqServiceIF reqService(
      @NonNull AbstractSubscriberService abstractSubscriberService,
      @NonNull NotifierService notifierService) {
    log.debug("loaded canonical ReqService bean");
    return new ReqService(abstractSubscriberService, notifierService);
  }
}
