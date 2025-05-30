package com.prosilion.superconductor.service;

import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.util.FilterMatcher;
import java.util.List;
import java.util.Optional;
import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.Kind;
import nostr.event.filter.AddressTagFilter;
import nostr.event.filter.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.AddressTag;
import nostr.event.tag.IdentifierTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class FilterMatcherIT {
  Kind kind = Kind.TEXT_NOTE;
  String author = "f1b419a95cb0233a11d431423b41a42734e7165fcab16081cd08ef1c90e0be75";
  PublicKey publicKey = new PublicKey(author);
  IdentifierTag identifierTag = new IdentifierTag("UUID-1");
  Relay relay = new Relay("ws://localhost:8080");

  FilterMatcher<GenericEvent> filterMatcher;

  @Autowired
  public FilterMatcherIT(FilterMatcher<GenericEvent> filterMatcher) {
    this.filterMatcher = filterMatcher;
  }

  @Test
  void testEquality() {
    AddressTag addressTag = new AddressTag();
    addressTag.setKind(kind.getValue());
    addressTag.setPublicKey(publicKey);

    Filters filters = new Filters(
        new AddressTagFilter<>(addressTag));

    GenericEvent event = new GenericEvent(publicKey, kind, List.of(addressTag));
    AddNostrEvent<GenericEvent> one = new AddNostrEvent<>(event);

    Optional<AddNostrEvent<GenericEvent>> resultOne = filterMatcher.intersectFilterMatches(filters, one);
    assertDoesNotThrow(resultOne::get);
    assertEquals(resultOne.orElseThrow().event(), event);
    assertTrue(resultOne.orElseThrow().event().getTags().contains(addressTag));

//    with identifierTag
    AddressTag addressTag2 = new AddressTag();
    addressTag2.setKind(kind.getValue());
    addressTag2.setPublicKey(publicKey);
    IdentifierTag identifierTag2 = new IdentifierTag("UUID-B");
    addressTag2.setIdentifierTag(identifierTag2);

    Filters filters2 = new Filters(
        new AddressTagFilter<>(addressTag2));

    GenericEvent event2 = new GenericEvent(publicKey, kind, List.of(addressTag2));
    AddNostrEvent<GenericEvent> two = new AddNostrEvent<>(event2);

    Optional<AddNostrEvent<GenericEvent>> resultTwo = filterMatcher.intersectFilterMatches(filters2, two);
    assertDoesNotThrow(resultTwo::get);
    assertEquals(resultTwo.orElseThrow().event(), event2);
    assertTrue(resultTwo.orElseThrow().event().getTags().contains(addressTag2));

//    with Relay
    AddressTag addressTag3 = new AddressTag();
    addressTag3.setKind(kind.getValue());
    addressTag3.setPublicKey(publicKey);
    IdentifierTag identifierTag3 = new IdentifierTag("UUID-A");
    addressTag3.setIdentifierTag(identifierTag3);

    Filters filters3 = new Filters(
        new AddressTagFilter<>(addressTag3));

    GenericEvent event3 = new GenericEvent(publicKey, kind, List.of(addressTag3));
    AddNostrEvent<GenericEvent> three = new AddNostrEvent<>(event3);

    Optional<AddNostrEvent<GenericEvent>> resultThree = filterMatcher.intersectFilterMatches(filters3, three);
    assertDoesNotThrow(resultThree::get);
    assertEquals(resultThree.orElseThrow().event(), event3);
    assertTrue(resultThree.orElseThrow().event().getTags().contains(addressTag3));

//    test non-matching
    Optional<AddNostrEvent<GenericEvent>> resultFailThreeNonMatch = filterMatcher.intersectFilterMatches(filters3, one);
    assertThrows(Exception.class, resultFailThreeNonMatch::get);

    AddressTag addressTag4 = new AddressTag();
    addressTag4.setKind(kind.getValue());
    addressTag4.setPublicKey(publicKey);
    IdentifierTag identifierTag4 = new IdentifierTag("UUID-B");
    addressTag4.setIdentifierTag(identifierTag4);

    GenericEvent event4 = new GenericEvent(publicKey, kind, List.of(addressTag4));
    AddNostrEvent<GenericEvent> four = new AddNostrEvent<>(event4);
    Optional<AddNostrEvent<GenericEvent>> resultFour = filterMatcher.intersectFilterMatches(filters3, four);

    assertThrows(Exception.class, resultFour::get);
    assertThrows(Exception.class, resultFour::orElseThrow);
  }
}
