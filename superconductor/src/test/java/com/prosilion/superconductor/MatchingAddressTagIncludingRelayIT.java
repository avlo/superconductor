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
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.service.event.type.EventEntityService;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
  private final String eventId = Factory.generateRandomHex64String();
  private final PublicKey publicKey = Identity.generateRandomIdentity().getPublicKey();
  private final PublicKey aTagPubkey = Identity.generateRandomIdentity().getPublicKey();
  private final String uuid = Factory.generateRandomHex64String();

  @Autowired
  MatchingAddressTagIncludingRelayIT(
      @NonNull NostrRelayService nostrRelayService,
      @NonNull EventEntityService eventEntityService) throws IOException {
    this.nostrRelayService = nostrRelayService;
    assertTrue(
        nostrRelayService.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());

    Optional<EventEntity> byEventIdString = eventEntityService.findByEventIdString(eventId);
    GenericEventKindIF eventById = eventEntityService.getEventById(byEventIdString.orElseThrow().getId());

    String string = Objects.requireNonNull(Filterable.getTypeSpecificTags(AddressTag.class, eventById)
        .stream()
        .findFirst().orElseThrow()
        .getRelay().getUri().toString());

    assertEquals(
        WS_LOCALHOST_5555,
        string);
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException, URISyntaxException {
    String subscriberId = Factory.generateRandomHex64String();
    IdentifierTag identifierTag = new IdentifierTag(uuid);

    AddressTag addressTag = new AddressTag(
        Kind.TEXT_NOTE, aTagPubkey, identifierTag, new Relay(WS_LOCALHOST_5555));

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(new AddressTagFilter(addressTag)));
    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEventKindIF> returnedEvents = getGenericEventKindIFs(returnedBaseMessages);
    log.debug("okMessage:");
    log.debug("  " + returnedEvents);

    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getTags().stream().anyMatch(baseTag -> baseTag.equals(addressTag))));

    assertTrue(returnedEvents.stream().anyMatch(s -> s.getTags().stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast)
        .anyMatch(tag -> {
          Assertions.assertNotNull(tag.getIdentifierTag());
          return tag.getIdentifierTag().equals(identifierTag);
        })));
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"matching address tag filter test\",\n" +
        "    \"id\": \"" + eventId + "\",\n" +
        "    \"kind\": 1,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"" + publicKey + "\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"a\",\n" +
        "        \"1:" + aTagPubkey + ":" + uuid + "\",\"ws://localhost:5555\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
