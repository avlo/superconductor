package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.TextNoteEventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingReferencedPubkeyIT {
  private final NostrRelayService nostrRelayService;
  private final String eventId = Factory.generateRandomHex64String();
  private final PublicKey referencedPubkey = Identity.generateRandomIdentity().getPublicKey();

  @Autowired
  MatchingReferencedPubkeyIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    PubKeyTag pubKeyTag = new PubKeyTag(referencedPubkey);
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedPublicKeyFilter(pubKeyTag)));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    assertTrue(returnedEvents.stream()
        .map(event -> event.getTags().stream()
            .filter(PubKeyTag.class::isInstance)
            .map(PubKeyTag.class::cast)
            .anyMatch(pk -> pk.getPublicKey().equals(referencedPubkey))).findFirst().orElseThrow());

    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  void testReqNonMatchingReferencedPubkey() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    String nonMatchingReferencedPubKey = Factory.generateRandomHex64String();

    PublicKey publicKey = new PublicKey(nonMatchingReferencedPubKey);
    PubKeyTag pubKeyTag = new PubKeyTag(publicKey);
    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedPublicKeyFilter(pubKeyTag)));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    assertTrue(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"matching identity filter test\",\n" +
        "    \"id\":\"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"" + Factory.generateRandomHex64String() + "\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"a\",\n" +
        "        \"30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\",\n" +
        "        \"wss://nostr.example.com\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"custom-tag\",\n" +
        "        \"custom-tag random value\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"p\",\n" +
        "        \"" + referencedPubkey.toHexString() + "\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"e\",\n" +
        "        \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"\n" +
        "      ],\n" +
        "      [\n" +
        "        \"g\",\n" +
        "        \"textnote geo-tag-1\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
