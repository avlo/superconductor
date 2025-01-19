package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Nested
class MultipleSubcsriberClassifiedListingEventMessageIT extends AbstractMultipleSubscriber {
  private final String uuidPrefix;

  @Autowired
  MultipleSubcsriberClassifiedListingEventMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);
    this.uuidPrefix = uuidPrefix;
  }

  public String getGlobalEventJson(String startEventId) {
    return "[\"EVENT\", {\"id\":\"" + startEventId + "\", \"kind\": 30402, \"content\": \"classified content\", \"tags\": [[\"subject\", \"classified subject\"], [\"title\", \"classified title\"], [\"published_at\", \"1718843251760\"], [\"summary\", \"classified summary\"], [\"location\", \"classified peroulades\"], [\"price\", \"271.00\", \"BTC\", \"1\"], [\"e\", \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"], [\"p\", \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"], [\"t\", \"classified hash-tag-1111\"], [\"g\", \"classified geo-tag-1\" ]], \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\", \"created_at\": 1718843251760, \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  public String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\", \"tags\": [[\"subject\", \"classified subject\"], [\"title\", \"classified title\"], [\"published_at\", \"1718843251760\"], [\"summary\", \"classified summary\"], [\"location\", \"classified peroulades\"], [\"price\", \"271.00\", \"BTC\", \"1\"], [\"e\", \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"], [\"p\", \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"], [\"t\", \"classified hash-tag-1111\"], [\"g\", \"classified geo-tag-1\" ]], \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\", \"created_at\": 1718843251760, \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\", \"kind\": 30402, \"content\": \"classified content\"}";
  }

  public String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
//    return "[\"REQ\",\"" + uuidKey + "\",{\"ids\":[\"" + uuid + "\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
  }
}
