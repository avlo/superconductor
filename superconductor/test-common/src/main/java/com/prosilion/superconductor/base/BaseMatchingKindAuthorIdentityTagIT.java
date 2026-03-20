package com.prosilion.superconductor.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.base.util.NostrComprehensiveClient;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public abstract class BaseMatchingKindAuthorIdentityTagIT {
  private final NostrComprehensiveClient nostrComprehensiveClient;
  private static final String uuid = Factory.generateRandomHex64String();
  private static final String eventId = Factory.generateRandomHex64String();
  private static final String authorPubKey = Factory.generateRandomHex64String();
  private static final String content = Factory.lorumIpsum();

  public BaseMatchingKindAuthorIdentityTagIT(
      @NonNull String relayUrl,
      Duration requestTimeoutDuration) throws IOException {
    this.nostrComprehensiveClient = new NostrComprehensiveClient(relayUrl, requestTimeoutDuration);
    assertTrue(
        nostrComprehensiveClient.send(
                (EventMessage) BaseMessageDecoder.decode(getEvent()))
            .getFlag());
  }

  @Test
  void testReqMessagesViaReqMessage() throws JsonProcessingException, NostrException {
    final String subscriberId = Factory.generateRandomHex64String();

    KindFilter kindFilter = new KindFilter(Kind.CALENDAR_TIME_BASED_EVENT);
    AuthorFilter authorFilter = new AuthorFilter(new PublicKey(authorPubKey));
    IdentifierTag identifierTag = new IdentifierTag(uuid);
    IdentifierTagFilter identifierTagFilter = new IdentifierTagFilter(identifierTag);

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            kindFilter, authorFilter, identifierTagFilter));

    List<BaseMessage> returnedBaseMessages = nostrComprehensiveClient.send(reqMessage);
    List<EventIF> returnedEvents = BaseTextNoteEventMessageIT.getEventIFs(returnedBaseMessages);
    log.debug("  " + returnedEvents);

    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getContent().equals(content)));
    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getTags().stream().anyMatch(baseTag -> baseTag.equals(identifierTag))));
  }

  private String getEvent() {
    return "[\n" +
        "  \"EVENT\",\n" +
        "  {\n" +
        "    \"content\": \"" + content + "\",\n" +
        "    \"id\": \"" + eventId + "\",\n" +
        "    \"kind\": 31923,\n" +
        "    \"created_at\": 1111111111111,\n" +
        "    \"pubkey\": \"" + authorPubKey + "\",\n" +
        "    \"tags\": [\n" +
        "      [\n" +
        "        \"d\",\n" +
        "        \"" + uuid + "\"\n" +
        "      ]\n" +
        "    ],\n" +
        "    \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"\n" +
        "  }\n" +
        "]\n";
  }
}
