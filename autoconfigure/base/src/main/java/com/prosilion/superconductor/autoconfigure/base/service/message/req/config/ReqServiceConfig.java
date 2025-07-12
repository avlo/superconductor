package com.prosilion.superconductor.autoconfigure.base.service.message.req.config;

import com.prosilion.superconductor.base.service.request.AbstractSubscriberService;
import com.prosilion.superconductor.base.service.request.NotifierService;
import com.prosilion.superconductor.base.service.request.ReqService;
import com.prosilion.superconductor.base.service.request.ReqServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
public class ReqServiceConfig {

  @Bean
  ReqServiceIF reqService(
      @NonNull AbstractSubscriberService abstractSubscriberService,
      @NonNull NotifierService notifierService) {
    log.debug("loaded canonical ReqService bean");
    return new ReqService(abstractSubscriberService, notifierService);
  }
}
