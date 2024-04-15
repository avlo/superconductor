package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.TextNoteEvent;
import nostr.event.tag.EventTag;
import nostr.event.tag.PriceTag;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class EventNotifierEngineTest {
  public static PublicKey PUB_KEY_TEXTNOTE_1;
  public static String hexPubKey1 = "aaa73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70";
  public static PublicKey PUB_KEY_TEXTNOTE_2;
  public static String hexPubKey2 = "bbb73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70";
  public static final PublicKey PUB_KEY_TEXTNOTE_3 = new PublicKey("ccc73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  public static final PublicKey PUB_KEY_CLASSIFIED_1 = new PublicKey("ddd73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  public static final PublicKey PUB_KEY_CLASSIFIED_2 = new PublicKey("eee73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  public static final PublicKey PUB_KEY_CLASSIFIED_3 = new PublicKey("fff73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  public static final String TEXT_NOTE_EVENT_1 = "TEXT-NOTE-EVENT-11111";
  public static final String TEXT_NOTE_EVENT_2 = "TEXT-NOTE-EVENT-2222";
  public static final String TEXT_NOTE_EVENT_3 = "TEXT-NOTE-EVENT-3333";
  public static final String CLASSIFIED_BASETAG_1 = "CLASSIFIED-BASE-TAG-11111";
  public static final String CLASSIFIED_BASETAG_2 = "CLASSIFIED-BASE-TAG-2222";
  public static final String CLASSIFIED_BASETAG_3 = "CLASSIFIED-BASE-TAG-3333";

  private static final String CONTENT = "CONTENT";
  @MockBean
  private static ApplicationEventPublisher publisher;
  private static EventNotifierEngine eventNotifierEngine;

  @BeforeAll
  public static void setup() {
    eventNotifierEngine = new EventNotifierEngine(publisher);
    PUB_KEY_TEXTNOTE_1 = new PublicKey(hexPubKey1);
    PUB_KEY_TEXTNOTE_2 = new PublicKey(hexPubKey2);
  }

  @Test
  @Order(1)
  void addTwoTextNoteEvents() {
    TextNoteEvent textNoteEvent1 = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_1,
        List.of(new EventTag(TEXT_NOTE_EVENT_1)),
        CONTENT
    );
    textNoteEvent1.setId("1111111111");
    textNoteEvent1.setKind(Kind.TEXT_NOTE.getValue());

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<TextNoteEvent>(
        Long.valueOf(textNoteEvent1.getId()),
        textNoteEvent1,
        Kind.valueOf(
            textNoteEvent1.getKind()
        ))
    );

    Map<Kind, Map<Long, TextNoteEvent>> kindEventMap = eventNotifierEngine.getKindEventMap();
    Assertions.assertEquals(1, kindEventMap.size());
    Assertions.assertEquals(1, kindEventMap.get(Kind.TEXT_NOTE).size());

    TextNoteEvent textNoteEvent2 = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_2,
        List.of(new EventTag(TEXT_NOTE_EVENT_2)),
        CONTENT
    );
    textNoteEvent2.setId("2222222222");
    textNoteEvent2.setKind(Kind.TEXT_NOTE.getValue());

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<TextNoteEvent>(
        Long.valueOf(textNoteEvent2.getId()),
        textNoteEvent2,
        Kind.valueOf(
            textNoteEvent2.getKind()
        ))
    );

    kindEventMap = eventNotifierEngine.getKindEventMap();
    Assertions.assertEquals(1, kindEventMap.size());
    Assertions.assertEquals(2, kindEventMap.get(Kind.TEXT_NOTE).size());
  }

  @Test
  @Order(2)
  void addSingleTextNoteEvent() {
    TextNoteEvent textNoteEvent = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_3,
        List.of(new EventTag(TEXT_NOTE_EVENT_3)),
        CONTENT
    );

    textNoteEvent.setId("333333333333333333");
    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<TextNoteEvent>(
        Long.valueOf(textNoteEvent.getId()),
        textNoteEvent,
        Kind.valueOf(
            textNoteEvent.getKind()
        ))
    );

    Map<Kind, Map<Long, TextNoteEvent>> kindEventMap = eventNotifierEngine.getKindEventMap();
    Assertions.assertEquals(1, kindEventMap.size());
    Assertions.assertEquals(3, kindEventMap.get(Kind.TEXT_NOTE).size());
  }

  @Test
  @Order(3)
  void addSingleClassifiedEvent() {
    ClassifiedListingEvent classifiedEvent = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_3,
        List.of(new EventTag(CLASSIFIED_BASETAG_3)),
        CONTENT,
        new ClassifiedListing(
            "classified title 3333",
            "classified summarysummary 3333",
            new PriceTag("333", "USD", "1")
        ));
    classifiedEvent.setId("33333333333333");
    classifiedEvent.setKind(Kind.CLASSIFIED_LISTING.getValue());

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<ClassifiedListingEvent>(
        Long.valueOf(classifiedEvent.getId()),
        classifiedEvent,
        Kind.valueOf(
            classifiedEvent.getKind()
        ))
    );
    Map<Kind, Map<Long, TextNoteEvent>> kindEventMap = eventNotifierEngine.getKindEventMap();
    Assertions.assertEquals(2, kindEventMap.size());
    Assertions.assertEquals(3, kindEventMap.get(Kind.TEXT_NOTE).size());
    Assertions.assertEquals(1, kindEventMap.get(Kind.CLASSIFIED_LISTING).size());
  }

  @Test
  @Order(4)
  void testTwoClassifiedEvents() {
    ClassifiedListingEvent classifiedEvent1 = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_1,
        List.of(new EventTag(CLASSIFIED_BASETAG_1)),
        CONTENT,
        new ClassifiedListing(
            "classified title 111111",
            "classified summarysummary 11111",
            new PriceTag("1111", "BTC", "1")
        ));
    classifiedEvent1.setId("1111111111");
    classifiedEvent1.setKind(Kind.CLASSIFIED_LISTING.getValue());

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<ClassifiedListingEvent>(
        Long.valueOf(classifiedEvent1.getId()),
        classifiedEvent1,
        Kind.valueOf(
            classifiedEvent1.getKind()
        ))
    );

    Map<Kind, Map<Long, TextNoteEvent>> kindEventMap = eventNotifierEngine.getKindEventMap();
    Assertions.assertEquals(2, kindEventMap.size());
    Assertions.assertEquals(3, kindEventMap.get(Kind.TEXT_NOTE).size());
    Assertions.assertEquals(2, kindEventMap.get(Kind.CLASSIFIED_LISTING).size());

    ClassifiedListingEvent classifiedEvent2 = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_2,
        List.of(new EventTag(CLASSIFIED_BASETAG_2)),
        CONTENT,
        new ClassifiedListing(
            "classified title 2222",
            "classified summarysummary 22222",
            new PriceTag("2222", "USD", "1")
        ));
    classifiedEvent2.setId("222222222222222");
    classifiedEvent2.setKind(Kind.CLASSIFIED_LISTING.getValue());

    eventNotifierEngine.nostrEventHandler(new AddNostrEvent<ClassifiedListingEvent>(
        Long.valueOf(classifiedEvent2.getId()),
        classifiedEvent2,
        Kind.valueOf(
            classifiedEvent2.getKind()
        ))
    );

    kindEventMap = eventNotifierEngine.getKindEventMap();
    Assertions.assertEquals(2, kindEventMap.size());
    Assertions.assertEquals(3, kindEventMap.get(Kind.TEXT_NOTE).size());
    Assertions.assertEquals(3, kindEventMap.get(Kind.CLASSIFIED_LISTING).size());
  }
}