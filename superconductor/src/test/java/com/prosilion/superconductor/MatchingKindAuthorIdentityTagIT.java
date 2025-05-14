package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.PublicKey;
import nostr.event.BaseMessage;
import nostr.event.Kind;
import nostr.event.filter.AuthorFilter;
import nostr.event.filter.Filters;
import nostr.event.filter.IdentifierTagFilter;
import nostr.event.filter.KindFilter;
import nostr.event.impl.GenericEvent;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EventMessage;
import nostr.event.message.ReqMessage;
import nostr.event.tag.IdentifierTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static com.prosilion.superconductor.EventMessageIT.getGenericEvents;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class MatchingKindAuthorIdentityTagIT {
  private final NostrRelayService nostrRelayService;
  private final static String uuidFromFile = "superconductor_subscriber_id-0";
  private final static String eventId = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  private final static String authorPubKey = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
  private final static String content = "matching kind, author, identity-tag filter test";

  @Autowired
  MatchingKindAuthorIdentityTagIT(@NonNull NostrRelayService nostrRelayService) throws IOException {
    this.nostrRelayService = nostrRelayService;

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/matching_kind_author_identitytag_filter_input.json"))) {
      String textMessageEventJson = lines.collect(Collectors.joining("\n"));
      log.debug("setup() send event:\n  {}", textMessageEventJson);
      assertTrue(
          nostrRelayService.send(
                  new BaseMessageDecoder<EventMessage>().decode(textMessageEventJson))
              .getFlag());
    }
  }

  @Test
  void testReqMessagesViaReqMessage() throws JsonProcessingException {
    final String subscriberId = Factory.generateRandomHex64String();

    KindFilter<Kind> kindFilter = new KindFilter<>(Kind.CALENDAR_TIME_BASED_EVENT);
    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(new PublicKey(authorPubKey));
    IdentifierTag identifierTag = new IdentifierTag(uuidFromFile);
    IdentifierTagFilter<IdentifierTag> identifierTagFilter = new IdentifierTagFilter<>(identifierTag);

    ReqMessage reqMessage = new ReqMessage(subscriberId,
        new Filters(
            kindFilter, authorFilter, identifierTagFilter));

    List<BaseMessage> returnedBaseMessages = nostrRelayService.send(reqMessage);
    List<GenericEvent> returnedEvents = getGenericEvents(returnedBaseMessages);
    log.debug("  " + returnedEvents);

    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getId().equals(eventId)));
    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getContent().equals(content)));
    assertTrue(returnedEvents.stream().anyMatch(event ->
        event.getTags().stream().anyMatch(baseTag -> baseTag.equals(identifierTag))));
  }
}
