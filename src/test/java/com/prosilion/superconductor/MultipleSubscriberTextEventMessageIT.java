package com.prosilion.superconductor;

import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Nested
class MultipleSubscriberTextEventMessageIT extends AbstractMultipleSubscriber {

  private final String authorPubKey;
  private final String addressableTagAuthorPubKey;
  private final String content;

  private final String pubKeyTagPubKey;
  private final String customTagValue;
  private final String eventTagId;
  private final String geoTagText;

  @Autowired
  MultipleSubscriberTextEventMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);

    this.authorPubKey = Factory.generateRandomHex64String();
    this.addressableTagAuthorPubKey = Factory.generateRandomHex64String();
    this.content = Factory.lorumIpsum(getClass());
    this.pubKeyTagPubKey = Factory.generateRandomHex64String();
    this.customTagValue = Factory.generateRandomHex64String();
    this.eventTagId = Factory.generateRandomHex64String();
    this.geoTagText = Factory.generateRandomHex64String();
  }

  public String getGlobalEventJson(String startEventId) {
    return "[ \"EVENT\", { \"content\": \"" + content + "\", \"id\":\"" + startEventId + "\", \"kind\": 1, \"created_at\": 1717357053050, \"pubkey\": \"" + authorPubKey + 
        "\", \"tags\": [[\"a\", \"30023:" + addressableTagAuthorPubKey + ":abcd\"]," +
        "[\"custom-tag\", \"" + customTagValue + "\"], [\"p\", \"" + pubKeyTagPubKey + "\"], [\"e\", \"" + eventTagId + "\"], [\"g\", \"" + geoTagText + "\"]], \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  public String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\", \"kind\": 1, \"created_at\": 1717357053050, \"pubkey\": \"" + authorPubKey +
        "\", \"tags\": [[\"a\", \"30023:" + addressableTagAuthorPubKey + ":abcd\"], [\"custom-tag\", \"" + customTagValue + "\"], [\"p\", \"" + pubKeyTagPubKey + "\"], [\"e\", \"" + eventTagId + "\"], [\"g\", \"" + geoTagText + "\"]], \"content\": \"" + content + "\", \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}";
  }

  public String createReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }
}
