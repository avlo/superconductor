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
class MultipleZapRequestEventMessageIT extends AbstractMultipleSubscriber {
  private final String authorPubKey;
  private final String content;
  private final String hashTagText;
  private final String lnUrl;

  private final String pubKeyTagPubKey;
  private final String subject;
  private final String eventTagId;
  private final String geoTagText;

  @Autowired
  MultipleZapRequestEventMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);

    this.eventTagId = Factory.generateRandomHex64String();
    this.authorPubKey = Factory.generateRandomHex64String();
    this.content = Factory.lorumIpsum(getClass());
    this.subject = Factory.lorumIpsum(getClass());
    this.hashTagText = Factory.lorumIpsum(getClass());
    this.lnUrl = Factory.lnUrl();
    this.pubKeyTagPubKey = Factory.generateRandomHex64String();
    this.geoTagText = Factory.lorumIpsum(getClass());
  }

  public String getGlobalEventJson(String startEventId) {
    return "[\"EVENT\",{\"id\":\"" + startEventId + "\", \"kind\": 9734, \"content\": \"" + content + "\", \"pubkey\": \"" + authorPubKey + "\", \"created_at\": 1719016694217, \"tags\": [[\"e\",\"" + eventTagId + "\"], [\"g\",\"" + geoTagText + "\"], [\"t\",\"" + hashTagText + "\"], [\"p\",\"" + pubKeyTagPubKey + "\"], [\"relays\",\"wss://localhost:5555\"], [\"subject\",\"" + subject + "\"], [\"amount\",\"271.00\"], [\"lnurl\",\"" + lnUrl + "\"]], \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  public String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\", \"pubkey\": \"" + authorPubKey + "\", \"created_at\": 1719016694217, \"tags\": [[\"e\",\"" + eventTagId + "\"], [\"p\",\"" + pubKeyTagPubKey + "\"], [\"g\",\"" + geoTagText + "\"], [\"t\",\"" + hashTagText + "\"], [\"relays\",\"wss://localhost:5555\"], [\"subject\",\"" + this.subject + "\"], [\"amount\",\"271.00\"], [\"lnurl\",\"" + lnUrl + "\"]], \"kind\": 9734, \"content\": \"" + content + "\", \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}";
  }

  public String createReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
//    return "[\"REQ\",\"" + uuidKey + "\",{\"ids\":[\"" + uuid + "\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
  }
}
