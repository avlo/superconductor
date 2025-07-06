package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.service.event.type.EventEntityService;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.TextNoteEventMessageIT.getGenericEventKindIFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingAddressTagIncludingRelayIT {
  public static final String WS_LOCALHOST_5555 = "ws://localhost:5555";
  private final NostrRelayService nostrRelayService;

  @Autowired
  MatchingAddressTagIncludingRelayIT(
      @NonNull NostrRelayService nostrRelayService,
      @NonNull EventEntityService eventEntityService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_address_tag_with_relay_single_filter_json_input.txt"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      EventMessage decode = (EventMessage) BaseMessageDecoder.decode(textMessageEventJson);
      assertTrue(
          nostrRelayService.send(
                  decode)
              .getFlag());

      Optional<EventEntity> byEventIdString = eventEntityService.findByEventIdString("6e77a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc");
      GenericEventKindIF eventById = eventEntityService.getEventById(byEventIdString.orElseThrow().getId());

      String string = Objects.requireNonNull(Filterable.getTypeSpecificTags(AddressTag.class, eventById)
          .stream()
          .findFirst().orElseThrow()
          .getRelay().getUri().toString());
      
      assertEquals(
          WS_LOCALHOST_5555,
          string);
    }
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException, URISyntaxException {
    String subscriberId = Factory.generateRandomHex64String();
    PublicKey publicKey = new PublicKey("babc33b02998a4a5600ecb6203e6efbe550074348a49d921060ff3225a123dc1");
    IdentifierTag identifierTag = new IdentifierTag("UUID-1");

    AddressTag addressTag = new AddressTag(
        Kind.TEXT_NOTE, publicKey, identifierTag, new Relay(WS_LOCALHOST_5555));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(new AddressTagFilter(addressTag)));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);

    assertTrue(returnedEvents.stream().anyMatch(s -> s.getTags().stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast)
        .anyMatch(tag -> {
          Assertions.assertNotNull(tag.getIdentifierTag());
          return tag.getIdentifierTag().equals(identifierTag);
        })));

    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getTags().stream().anyMatch(baseTag -> baseTag.equals(addressTag))));
  }
}
