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
class MultipleSubscriberTextEventMessageIT extends AbstractMultipleSubscriber {
  private final String uuidPrefix;

  @Autowired
  MultipleSubscriberTextEventMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);
    this.uuidPrefix = uuidPrefix;
  }

  public String getGlobalEventJson(String startEventId) {
    return "[ \"EVENT\", { \"content\": \"1111111111\", \"id\":\"" + startEventId + "\", \"kind\": 1, \"created_at\": 1717357053050, \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\", \"tags\": [[\"a\", \"wss://nostr.example.com\", \"30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\"], [\"custom-tag\", \"custom-tag random value\"], [\"p\", \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"], [\"e\", \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"], [\"g\", \"textnote geo-tag-1\"]], \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  public String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\", \"kind\": 1, \"created_at\": 1717357053050, \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\", \"tags\": [[\"a\", \"wss://nostr.example.com\", \"30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\"], [\"custom-tag\", \"custom-tag random value\"], [\"p\", \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"], [\"e\", \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"], [\"g\", \"textnote geo-tag-1\"]], \"content\": \"1111111111\", \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}";
  }

  public String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuidKey + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }
}
