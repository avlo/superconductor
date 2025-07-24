package com.prosilion.superconductor.redis.noop;

import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.OkMessage;
import com.prosilion.superconductor.redis.util.Factory;
import com.prosilion.superconductor.redis.util.NostrRelayServiceRedis;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("testnoop")
class EventNoOpMessageIT {
  private final NostrRelayServiceRedis nostrRelayService;

  private final String authorPubKey;
  private final String eventId;

  @Autowired
  EventNoOpMessageIT(@NonNull NostrRelayServiceRedis nostrRelayService) {
    this.nostrRelayService = nostrRelayService;
    this.eventId = Factory.generateRandomHex64String();
    this.authorPubKey = Factory.generateRandomHex64String();
  }

  @Test
  void testEventNoOpMessage() throws IOException {
    String content = Factory.lorumIpsum(getClass());
    String globalEventJson =
        "[\"EVENT\",{" +
            "\"id\":\"" + eventId +
            "\",\"kind\":1,\"content\":\"" + content +
            "\",\"pubkey\":\"" + authorPubKey +
            "\",\"created_at\":1717357053050" +
            ",\"tags\":[]" +
            ",\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
    
    log.debug("setup() send event:\n  {}", globalEventJson);

    OkMessage okMessage = nostrRelayService.send(
        (EventMessage) BaseMessageDecoder.decode(globalEventJson));

    final String noOpResponse = "application-testnoop.properties afterimage is a nostr-reputation authority relay.  it does not accept events, only requests";

    assertFalse(okMessage.getFlag());
    assertEquals(noOpResponse, okMessage.getMessage());
  }
}
