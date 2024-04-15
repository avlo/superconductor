package com.prosilion.nostrrelay.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import com.prosilion.nostrrelay.pubsub.AddSubscriberFiltersEvent;
import nostr.api.Nostr;
import nostr.base.PublicKey;
import nostr.event.BaseEvent;
import nostr.event.Kind;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.Filters;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.list.EventList;
import nostr.event.list.FiltersList;
import nostr.event.tag.EventTag;
import nostr.event.tag.PriceTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
class SubscriberFilterTriggerEventNotifierTest {
  public static PublicKey PUB_KEY_TEXTNOTE_1;
  public static final PublicKey PUB_KEY_CLASSIFIED_2 = new PublicKey("fff73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  public static final String CLASSIFIED_BASETAG_2 = "CLASSIFIED-BASE-TAG-22222";
  public static final String TEXT_NOTE_EVENT_1 = "TEXT-NOTE-EVENT-11111";
  private static final String hexPubKey1 = "aaa73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70";
  private static final String FILENAME = "trigger-text.txt";
  private static final String CONTENT = "CONTENT";

  private static final String EVENT_ID_OF_INTEREST = "1111111111";
  private static final String CLASSIFIED_ID_OF_INTEREST = "22222222222";

  private static EventNotifierEngine eventNotifierEngine;

  @Autowired
  private static SubscriberNotifier subscriberNotifier;

  @MockBean
  private static ApplicationEventPublisher publisher;

  @BeforeAll
  public static void setup() {
    eventNotifierEngine = new EventNotifierEngine(publisher);
    PUB_KEY_TEXTNOTE_1 = new PublicKey(hexPubKey1);
  }

  @Test
  @Order(1)
  void addTextNoteEventAddClassifiedEvent() {
    TextNoteEvent textNoteEvent1 = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_1,
        List.of(new EventTag(TEXT_NOTE_EVENT_1)),
        CONTENT
    );
    textNoteEvent1.setId(EVENT_ID_OF_INTEREST);
    textNoteEvent1.setKind(Kind.TEXT_NOTE.getValue());
    textNoteEvent1.setCreatedAt(1712006760L);

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<TextNoteEvent>(
        11L,
        textNoteEvent1,
        Kind.valueOf(
            textNoteEvent1.getKind()
        ))
    );

    ClassifiedListing classifiedListing = new ClassifiedListing(
        "classified title 222",
        "classified summarysummary 2222",
        new PriceTag("222", "USD", "1")
    );
    classifiedListing.setPublishedAt(1712006760L);

    ClassifiedListingEvent classifiedEvent = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_2,
        List.of(new EventTag(CLASSIFIED_BASETAG_2)),
        CONTENT,
        classifiedListing);

    classifiedEvent.setId(CLASSIFIED_ID_OF_INTEREST);
    classifiedEvent.setKind(Kind.CLASSIFIED_LISTING.getValue());
    classifiedEvent.setCreatedAt(1712006760L);

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<ClassifiedListingEvent>(
        22L,
        classifiedEvent,
        Kind.valueOf(
            classifiedEvent.getKind()
        ))
    );
  }

  @Test
  @Order(2)
  void addSimpleSubscriberFilter() {
    final var filtersList = new FiltersList();
    filtersList.add(Filters.builder()
        .events(new EventList(new BaseEvent.ProxyEvent(EVENT_ID_OF_INTEREST)))
        .build()
    );

    eventNotifierEngine.addSubscriberFiltersHandler(new AddSubscriberFiltersEvent(
        1L,
        filtersList)
    );
  }

//  @Test
//  @Order(3)
//  void addFullyPopulatedSubscriberFilter() {
//    final var filtersList = new FiltersList();
//    filtersList.add(Filters.builder()
//        .events(new EventList(new BaseEvent.ProxyEvent(EVENT_ID_OF_INTEREST)))
//        .authors(new PublicKeyList(PUB_KEY_TEXTNOTE_1))
//        .kinds(new KindList(Kind.TEXT_NOTE.getValue(), Kind.CLASSIFIED_LISTING.getValue()))
//        .referencedEvents(new EventList(new BaseEvent.ProxyEvent(TEXT_NOTE_EVENT_1)))
//        .since(1712006760L)
//        .until(2712006760L)
//        .limit(1)
//        .build()
//    );
//
//    eventNotifierEngine.addSubscriberFiltersHandler(new AddSubscriberFiltersEvent(
//        2L,
//        filtersList)
//    );
//  }

  @Test
  @Order(99)
  void checker() {
    Map<Long, FiltersList> subscribersFiltersMap = eventNotifierEngine.getSubscribersFiltersMap();
    Map<Kind, Map<Long, GenericEvent>> kindEventMap = eventNotifierEngine.getKindEventMap();
    printText("SUBSCRIBER FILTERS\n\n", false);

    subscribersFiltersMap.forEach((name, student) -> {
      printText(String.format("%s: ", name.toString()), true);
      prettyPrintUsingGson(Optional.ofNullable(Nostr.Json.encode(student)).orElse(""), true);
    });

    printText("---------------------\n", true);
    printText("EVENT MAP\n\n", true);
    kindEventMap.forEach((letter, nestedMap) -> {
      nestedMap.forEach((name, student) -> {
        printText(String.format("%s: ", name.toString()), true);
        prettyPrintUsingGson(Optional.ofNullable(Nostr.Json.encode(student)).orElse(""), true);
      });
    });
  }

  private void printText(String printString, boolean append) {
    try {
      FileWriter fw = new FileWriter(FILENAME, append);
      fw.write(printString);
      fw.close();
    } catch (Exception e) {
      System.out.println(printString);
    }
  }

  private void prettyPrintUsingGson(String uglyJson, boolean append) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonElement jsonElement = JsonParser.parseString(uglyJson);
    printText(gson.toJson(jsonElement) + "\n\n", append);
  }
}