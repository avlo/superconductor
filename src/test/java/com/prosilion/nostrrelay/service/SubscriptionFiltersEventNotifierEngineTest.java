package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddSubscriberFiltersEvent;
import nostr.base.PublicKey;
import nostr.event.BaseEvent;
import nostr.event.Kind;
import nostr.event.impl.Filters;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.list.KindList;
import nostr.event.list.PublicKeyList;
import nostr.id.Identity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.prosilion.nostrrelay.service.EventNotifierEngineTest.TEXT_NOTE_EVENT_1;
import static com.prosilion.nostrrelay.service.EventNotifierEngineTest.hexPubKey1;

@ExtendWith(SpringExtension.class)
class SubscriptionFiltersEventNotifierEngineTest {
  public static PublicKey PUB_KEY_TEXTNOTE_1;
  public static String hexPubKey1 = "aaa73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70";
  private static EventNotifierEngine eventNotifierEngine;

  @BeforeAll
  public static void setup() {
    eventNotifierEngine = new EventNotifierEngine();
    PUB_KEY_TEXTNOTE_1 = new PublicKey(hexPubKey1);
  }

  @Test()
  @Order(1)
  void addSubscriber1Event() {
    final var filtersList = new FiltersList();
    filtersList.add(Filters.builder()
        .events(new EventList(new BaseEvent.ProxyEvent(hexPubKey1)))
        .authors(new PublicKeyList(PUB_KEY_TEXTNOTE_1))
        .kinds(new KindList(Kind.TEXT_NOTE.getValue(), Kind.CLASSIFIED_LISTING.getValue()))
        .referencedEvents(new EventList(new BaseEvent.ProxyEvent(TEXT_NOTE_EVENT_1)))
        .since(1712006760L)
        .until(2712006760L)
        .limit(1)
        .build()
    );
    eventNotifierEngine.addSubscriberFiltersHandler(new AddSubscriberFiltersEvent(1L, filtersList));
  }
}