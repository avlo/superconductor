package com.prosilion.superconductor.autoconfigure.base.config;

import com.prosilion.superconductor.autoconfigure.base.service.message.req.AutoConfigReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.RelayInfoDocService;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageService;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.ReqMessageServiceIF;
import com.prosilion.superconductor.autoconfigure.base.service.message.req.auth.AutoConfigReqMessageServiceNoAuthDecorator;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.message.RelayInfoDocServiceIF;
import com.prosilion.superconductor.base.service.request.ReqServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;

@Slf4j
@AutoConfiguration
@PropertySource("classpath:application-autoconfigure.properties")
public class ReqMessageServiceConfig {

  @Bean
  ReqMessageServiceIF reqMessageService(
      @NonNull ReqServiceIF reqService,
      @NonNull ClientResponseService clientResponseService) {
    log.debug("loaded ReqMessageService bean");
    return new ReqMessageService(reqService, clientResponseService);
  }

  @Bean
  @ConditionalOnProperty(name = "superconductor.auth.req.active", havingValue = "false", matchIfMissing = true)
  AutoConfigReqMessageServiceIF autoConfigReqMessageServiceIF(
      @NonNull ReqMessageServiceIF reqMessageServiceIF) {
    log.debug("loaded AutoConfigReqMessageServiceNoAuthDecorator bean (REQ NO-AUTH)");
    return new AutoConfigReqMessageServiceNoAuthDecorator(reqMessageServiceIF);
  }

  @Bean
  @ConditionalOnMissingBean
  RelayInfoDocServiceIF relayInfoDocService(
      @Value("${nostr.relay.description}") String descriptionValue,
      @Value("${nostr.relay.name}") String relayNameValue,
      @Value("${nostr.relay.pubkey}") String pubKeyValue,
      @Value("${nostr.relay.software}") String softwareKeyValue,
      @Value("${nostr.relay.supported_nips}") String supportedNipsValue,
      @Value("${nostr.relay.version}") String versionValue,
      @Value("${nostr.relay.payment_required}") String paymentRequiredValue,
      @Value("${nostr.relay.message_length}") String maxMessageLengthValue,
      @Value("${nostr.relay.max_event_tags}") String maxEventTagsValue,
      @Value("${nostr.relay.max_subscriptions}") String maxSubscriptionsValue,
      @Value("${nostr.relay.auth_required}") String authRequiredValue,
      @Value("${nostr.relay.payments.url}") String paymentsUrlValue,
      @Value("${nostr.relay.payments.amount}") String amountValue,
      @Value("${nostr.relay.payments.units}") String unitValue,
      @Value("${nostr.relay.payments.period}") String periodValue) {
    return new RelayInfoDocService(descriptionValue,
        relayNameValue,
        pubKeyValue,
        softwareKeyValue,
        supportedNipsValue,
        versionValue,
        paymentRequiredValue,
        maxMessageLengthValue,
        maxEventTagsValue,
        maxSubscriptionsValue,
        authRequiredValue,
        paymentsUrlValue,
        amountValue,
        unitValue,
        periodValue);
  }
}
