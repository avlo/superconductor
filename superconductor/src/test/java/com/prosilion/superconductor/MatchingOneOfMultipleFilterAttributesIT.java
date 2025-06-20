package com.prosilion.superconductor;

import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.ReferencedEventFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
class MatchingOneOfMultipleFilterAttributesIT {
  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingOneOfMultipleFilterAttributesIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_reference_event_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(
          nostrRelayService.send(
                  (EventMessage) BaseMessageDecoder.decode(textMessageEventJson))
              .getFlag());
    }
  }

  @Test
  void testMatchingOneOfMultipleReferencedEvents() throws IOException, ExecutionException, InterruptedException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    String referencedEventIdMatch = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346";
    String referencedEventIdNoMatch = "0000000000000000000000000000000000000000000000000000000000000000";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedEventFilter(
                new EventTag(referencedEventIdMatch)),
            new ReferencedEventFilter(
                new EventTag(referencedEventIdNoMatch))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    assertTrue(returnedEvents.stream().map(GenericEventKindIF::getId).anyMatch(s -> s.contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e590002")));
  }

  @Test
  void testMatchingOneOfMultipleReferencedPublicKeys() throws IOException, ExecutionException, InterruptedException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();

    String referencedPubkeyMatch = "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
    String referencedPubkeyNoMatch = "0000000000000000000000000000000000000000000000000000000000000000";

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            new ReferencedPublicKeyFilter(
                new PubKeyTag(new PublicKey(referencedPubkeyMatch))),
            new ReferencedPublicKeyFilter(
                new PubKeyTag(new PublicKey(referencedPubkeyNoMatch)))));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);

    log.debug("okMessage:");
    log.debug("  " + returnedBaseMessages);

    assertFalse(returnedEvents.isEmpty());
    assertFalse(returnedBaseMessages.isEmpty());

    assertTrue(returnedEvents.stream().map(GenericEventKindIF::getId).anyMatch(s -> s.contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e590002")));
  }
}
