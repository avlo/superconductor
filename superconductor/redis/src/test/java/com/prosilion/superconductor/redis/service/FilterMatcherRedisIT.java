package com.prosilion.superconductor.redis.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.util.FilterMatcher;
import com.prosilion.superconductor.lib.redis.dto.GenericDocumentKindDto;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
public class FilterMatcherRedisIT {
  Kind kind = Kind.TEXT_NOTE;
  String author = "f1b419a95cb0233a11d431423b41a42734e7165fcab16081cd08ef1c90e0be75";
  Identity identity = Identity.generateRandomIdentity();
  PublicKey publicKey = new PublicKey(author);

  FilterMatcher filterMatcher;

  @Autowired
  public FilterMatcherRedisIT(FilterMatcher filterMatcher) {
    this.filterMatcher = filterMatcher;
  }

  @Test
  void testEquality() throws NostrException, NoSuchAlgorithmException {
    AddressTag addressTag = new AddressTag(kind, publicKey);

    Filters filters = new Filters(
        new AddressTagFilter(addressTag));

    GenericEventKindIF event = new GenericDocumentKindDto(new TextNoteEvent(identity, List.of(addressTag), "content")).convertBaseEventToGenericEventKindIF();
    AddNostrEvent one = new AddNostrEvent(event);

    Optional<AddNostrEvent> resultOne = filterMatcher.intersectFilterMatches(filters, one);
    assertDoesNotThrow(resultOne::get);
    assertEquals(resultOne.orElseThrow().event(), event);
    assertTrue(resultOne.orElseThrow().event().getTags().contains(addressTag));

//    with identifierTag
    AddressTag addressTag2 = new AddressTag(kind, publicKey, new IdentifierTag("UUID-B"));

    Filters filters2 = new Filters(
        new AddressTagFilter(addressTag2));

    GenericEventKindIF event2 = new GenericDocumentKindDto(new TextNoteEvent(identity, List.of(addressTag2), "content")).convertBaseEventToGenericEventKindIF();
    AddNostrEvent two = new AddNostrEvent(event2);

    Optional<AddNostrEvent> resultTwo = filterMatcher.intersectFilterMatches(filters2, two);
    assertDoesNotThrow(resultTwo::get);
    assertEquals(resultTwo.orElseThrow().event(), event2);
    assertTrue(resultTwo.orElseThrow().event().getTags().contains(addressTag2));

//    with Relay
    AddressTag addressTag3 = new AddressTag(kind, publicKey, new IdentifierTag("UUID-A"), new Relay("ws://localhost:5555"));

    Filters filters3 = new Filters(
        new AddressTagFilter(addressTag3));

    GenericEventKindIF event3 = new GenericDocumentKindDto(new TextNoteEvent(identity, List.of(addressTag3), "content")).convertBaseEventToGenericEventKindIF();
    AddNostrEvent three = new AddNostrEvent(event3);

    Optional<AddNostrEvent> resultThree = filterMatcher.intersectFilterMatches(filters3, three);
    assertDoesNotThrow(resultThree::get);
    assertEquals(resultThree.orElseThrow().event(), event3);
    assertTrue(resultThree.orElseThrow().event().getTags().contains(addressTag3));

//    test non-matching
    Optional<AddNostrEvent> resultFailThreeNonMatch = filterMatcher.intersectFilterMatches(filters3, one);
    assertThrows(Exception.class, resultFailThreeNonMatch::get);

    AddressTag addressTag4 = new AddressTag(kind, publicKey, new IdentifierTag("UUID-B"));

    GenericEventKindIF event4 = new GenericDocumentKindDto(new TextNoteEvent(identity, List.of(addressTag4), "content")).convertBaseEventToGenericEventKindIF();
    AddNostrEvent four = new AddNostrEvent(event4);
    Optional<AddNostrEvent> resultFour = filterMatcher.intersectFilterMatches(filters3, four);

    assertThrows(Exception.class, resultFour::get);
    assertThrows(Exception.class, resultFour::orElseThrow);
  }
}
