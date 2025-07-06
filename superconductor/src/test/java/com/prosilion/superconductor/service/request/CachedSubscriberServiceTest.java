package com.prosilion.superconductor.service.request;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.entity.Subscriber;
import com.prosilion.superconductor.util.Factory;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CachedSubscriberServiceTest {
  Kind kind = Kind.TEXT_NOTE;
  String author = "f1b419a95cb0233a11d431423b41a42734e7165fcab16081cd08ef1c90e0be75";
  PublicKey publicKey = new PublicKey(author);
  IdentifierTag identifierTag = new IdentifierTag("UUID-1");
  Relay relay = new Relay(URI.create("ws://localhost:8080"));

  CachedSubscriberService cachedSubscriberService;

  @MockitoBean
  ApplicationEventPublisher publisher;

  public CachedSubscriberServiceTest() {
    this.cachedSubscriberService = new CachedSubscriberService(publisher);
  }

  @Test
  void testCheckIdenticalFilters() {
    String subscriberId = Factory.generateRandomHex64String();
    String sessionId = Factory.generateRandomHex64String();

    AddressTag addressTag = new AddressTag(
        kind, publicKey
    );

    Filters filters = new Filters(
        new AddressTagFilter(addressTag));
    List<Filters> filtersList = List.of(filters);

    Subscriber subscriber = new Subscriber(subscriberId, sessionId, true);
    Long subscriberSessionHash = cachedSubscriberService.save(subscriber, filtersList);
    assertTrue(cachedSubscriberService.checkIdenticalFilters(subscriberSessionHash, filtersList));

    Long subscriberSessionHashSame = cachedSubscriberService.save(subscriber, filtersList);
    assertEquals(subscriberSessionHash, subscriberSessionHashSame);

//  new and identical filters
    AddressTag addressTagDup = new AddressTag(
        kind, publicKey
    );

    Filters filtersDup = new Filters(
        new AddressTagFilter(addressTagDup));
    List<Filters> filtersListDup = List.of(filtersDup);

    assertTrue(cachedSubscriberService.checkIdenticalFilters(subscriberSessionHash, filtersListDup));
    Long subscriberSessionHashDup = cachedSubscriberService.save(subscriber, filtersListDup);
    assertEquals(subscriberSessionHash, subscriberSessionHashDup);

//    change minor variant, should fail
    AddressTag addressTagVariant = new AddressTag(Kind.RECOMMEND_SERVER, publicKey);

    Filters filtersVariant = new Filters(
        new AddressTagFilter(addressTagVariant));
    List<Filters> filtersListVariant = List.of(filtersVariant);

    assertFalse(cachedSubscriberService.checkIdenticalFilters(subscriberSessionHash, filtersListVariant));
    Long subscriberSessionHashVariant = cachedSubscriberService.save(subscriber, filtersListVariant);
    assertEquals(subscriberSessionHash, subscriberSessionHashVariant);

//    non matching
    AddressTag addressTag3 = new AddressTag(kind, publicKey, new IdentifierTag("UUID-A"));

    Filters filtersNew = new Filters(
        new AddressTagFilter(addressTag3));

    List<Filters> filtersNewList = List.of(filtersNew);
    assertFalse(cachedSubscriberService.checkIdenticalFilters(subscriberSessionHash, filtersNewList));

    assertThrows(IllegalArgumentException.class, () -> cachedSubscriberService.save(subscriber, List.of()));
  }

  @Test
  void testIdenticalListOfFilters() {
    String subscriberId = Factory.generateRandomHex64String();
    String sessionId = Factory.generateRandomHex64String();

    //  same list, diff order, should pass
    AddressTag addressTagList1A = new AddressTag(kind, publicKey);

    AddressTag addressTagList1B = new AddressTag(Kind.RECOMMEND_SERVER, publicKey);

    Filters filters1 = new Filters(
        new AddressTagFilter(addressTagList1A),
        new AddressTagFilter(addressTagList1B));

    Filters filters2 = new Filters(
        new AddressTagFilter(addressTagList1B),
        new AddressTagFilter(addressTagList1A));

    Subscriber subscriber = new Subscriber(subscriberId, sessionId, true);
    Long subscriberSessionHash = cachedSubscriberService.save(subscriber, List.of(filters1));
    assertTrue(cachedSubscriberService.checkIdenticalFilters(subscriberSessionHash, List.of(filters1)));

    Long subscriberSessionHashSame = cachedSubscriberService.save(subscriber, List.of(filters2));
    assertEquals(subscriberSessionHash, subscriberSessionHashSame);

//    non match, missing one list item
    Filters filters3 = new Filters(
        new AddressTagFilter(addressTagList1B));
    assertFalse(cachedSubscriberService.checkIdenticalFilters(subscriberSessionHash, List.of(filters3)));
    Long subscriberSessionHashMatch = cachedSubscriberService.save(subscriber, List.of(filters3));
    assertEquals(subscriberSessionHash, subscriberSessionHashMatch);
  }
}

