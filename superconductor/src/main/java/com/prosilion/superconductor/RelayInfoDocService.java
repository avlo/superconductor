package com.prosilion.superconductor;

import com.prosilion.nostr.event.BadgeDefinitionEvent;
import com.prosilion.superconductor.dto.GenericEventKindDto;
import com.prosilion.superconductor.service.event.type.EventPluginIF;
import com.prosilion.superconductor.service.message.RelayInfoDocServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

@Slf4j
@Service
public class RelayInfoDocService implements RelayInfoDocServiceIF {

  public static final String DESCRIPTION_KEY = "description";
  public final String descriptionValue;

  public static final String NAME = "name";
  public final String relayNameValue;

  public static final String PUBKEY_KEY = "pubkey";
  public final String pubKeyValue;

  public static final String SOFTWARE_KEY = "software";
  public final String softwareKeyValue;

  public static final String SUPPORTED_NIPS_KEY = "supported_nips";
  public final String supportedNipsValue;

  public static final String VERSION_KEY = "version";
  public final String versionValue;

  public static final String LIMITATION_LABEL = "limitation";

  public static final String PAYMENT_REQUIRED_KEY = "payment_required";
  public final String paymentRequiredValue;

  public static final String MAX_MESSAGE_LENGTH_KEY = "max_message_length";
  public final String maxMessageLengthValue;

  public static final String MAX_EVENT_TAGS_KEY = "max_event_tags";
  public final String maxEventTagsValue;

  public static final String MAX_SUBSCRIPTIONS_KEY = "max_subscriptions";
  public final String maxSubscriptionsValue;

  public static final String AUTH_REQUIRED_KEY = "auth_required";
  public final String authRequiredValue;

  public static final String PAYMENTS_URL_KEY = "payments_url";
  public final String paymentsUrlValue;

  public static final String FEES_LABEL = "fees";
  public static final String SUBSCRIPTION_LABEL = "subscription";

  public static final String AMOUNT_KEY = "amount";
  public final String amountValue;

  public static final String UNIT_KEY = "unit";
  public final String unitValue;

  public static final String PERIOD_KEY = "period";
  public final String periodValue;

  private final String nip11Json;

  @Autowired
  public RelayInfoDocService(
      @NonNull EventPluginIF eventPlugin,
      @NonNull BadgeDefinitionEvent upvoteBadgeDefinitionEvent,
      @NonNull BadgeDefinitionEvent downvoteBadgeDefinitionEvent,
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

//    TODO: relocate below badge definition DB entry creation to better location
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(upvoteBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());
    
//    TODO: relocate below badge definition DB entry creation to better location
    eventPlugin.processIncomingEvent(
        new GenericEventKindDto(downvoteBadgeDefinitionEvent).convertBaseEventToGenericEventKindIF());

    this.descriptionValue = descriptionValue;
    this.relayNameValue = relayNameValue;
    this.pubKeyValue = pubKeyValue;
    this.softwareKeyValue = softwareKeyValue;
    this.supportedNipsValue = supportedNipsValue;
    this.versionValue = versionValue;
    this.paymentRequiredValue = paymentRequiredValue;
    this.maxMessageLengthValue = maxMessageLengthValue;
    this.maxEventTagsValue = maxEventTagsValue;
    this.maxSubscriptionsValue = maxSubscriptionsValue;
    this.authRequiredValue = authRequiredValue;
    this.paymentsUrlValue = paymentsUrlValue;
    this.amountValue = amountValue;
    this.unitValue = unitValue;
    this.periodValue = periodValue;

    nip11Json = "{"
        + keyValue(NAME, relayNameValue)
        + keyValue(DESCRIPTION_KEY, descriptionValue)
        + keyValue(PUBKEY_KEY, pubKeyValue)
        + String.format("\"%s\":[\"%s\"],", SUPPORTED_NIPS_KEY, supportedNipsValue)
        + keyValue(SOFTWARE_KEY, softwareKeyValue)
        + keyValue(VERSION_KEY, versionValue)
        + String.format("\"%s\":{", LIMITATION_LABEL)
        + keyValue(PAYMENT_REQUIRED_KEY, paymentRequiredValue)
        + keyValue(MAX_MESSAGE_LENGTH_KEY, maxMessageLengthValue)
        + keyValue(MAX_EVENT_TAGS_KEY, maxEventTagsValue)
        + keyValue(MAX_SUBSCRIPTIONS_KEY, maxSubscriptionsValue)
        + String.format("\"%s\":\"%s\"},", AUTH_REQUIRED_KEY, authRequiredValue)
        + keyValue(PAYMENTS_URL_KEY, paymentsUrlValue)
        + String.format("\"%s\":{", FEES_LABEL)
        + String.format("\"%s\":[{", SUBSCRIPTION_LABEL)
        + keyValue(AMOUNT_KEY, amountValue)
        + keyValue(UNIT_KEY, unitValue)
        + String.format("\"%s\":\"%s\"", PERIOD_KEY, periodValue)
        + "}]}}";
  }

  private static String keyValue(String key, String value) {
    return String.format("\"%s\":\"%s\",", key, value);
  }

  @Override
  public WebSocketMessage<String> processIncoming() {
    log.debug("Send RelayInformationDocument: {}", nip11Json);
    return new TextMessage(nip11Json);
  }
}
