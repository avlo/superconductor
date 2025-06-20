package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EoseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.EventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingReferencedPubkeyIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingReferencedPubkeyIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_reference_pubkey_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(
          nostrRelayService.send(
                  (EventMessage) BaseMessageDecoder.decode(textMessageEventJson))
              .getFlag());
    }
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    String referencedPubKey = "2bff79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

    PublicKey publicKey = new PublicKey(referencedPubKey);
    PubKeyTag pubKeyTag = new PubKeyTag(publicKey);
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
            .anyMatch(pk -> pk.getPublicKey().toHexString().equals(referencedPubKey))).findFirst().orElseThrow());

    assertTrue(returnedBaseMessages.stream().anyMatch(EoseMessage.class::isInstance));
  }

  @Test
  void testReqNonMatchingReferencedPubkey() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    String nonMatchingReferencedPubKey = "cccd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";

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
}
