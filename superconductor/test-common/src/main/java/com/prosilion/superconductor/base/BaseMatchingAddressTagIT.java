package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.subdivisions.client.reactive.NostrEventPublisher;
import com.prosilion.subdivisions.client.reactive.NostrSingleRequestService;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.Utils;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseMatchingAddressTagIT {
  private final String eventId = Factory.generateRandomHex64String();
  private final PublicKey publicKey = Identity.generateRandomIdentity().getPublicKey();
  private final PublicKey aTagPubkey = Identity.generateRandomIdentity().getPublicKey();
  private final String uuid = Factory.generateRandomHex64String();
  private final String relayUrl;

  public BaseMatchingAddressTagIT(@NonNull String relayUrl) throws IOException {
    this.relayUrl = relayUrl;
    NostrEventPublisher nostrEventPublisher = new NostrEventPublisher(relayUrl);
    assertTrue(
        nostrEventPublisher.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testReqMessages() throws JsonProcessingException, NostrException {
    String subscriberId = Factory.generateRandomHex64String();
    IdentifierTag identifierTag = new IdentifierTag(uuid);

    AddressTag addressTag = new AddressTag(
        Kind.TEXT_NOTE, aTagPubkey, identifierTag
    );

    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(new AddressTagFilter(addressTag)));
    List<BaseMessage> returnedBaseMessages = new NostrSingleRequestService().send(reqMessage, relayUrl);
    List<EventIF> returnedEvents = Utils.getEventIFs(returnedBaseMessages);
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
        "        \"1:" + aTagPubkey + ":" + uuid + "\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
