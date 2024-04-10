package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.TextNoteEvent;
import nostr.event.tag.EventTag;
import nostr.event.tag.PriceTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
class EventNotifierEngineTest {
  private static final PublicKey PUB_KEY_TEXTNOTE_1 = new PublicKey("aaa73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_TEXTNOTE_2 = new PublicKey("bbb73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_TEXTNOTE_3 = new PublicKey("ccc73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_CLASSIFIED_1 = new PublicKey("ddd73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_CLASSIFIED_2 = new PublicKey("eee73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_CLASSIFIED_3 = new PublicKey("fff73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final String CONTENT = "CONTENT";
  private static EventNotifierEngine eventNotifierEngine;

  @BeforeAll
  public static void setup() {
    eventNotifierEngine = new EventNotifierEngine();
  }

  @Test()
  @Order(2)
  void addSingleTextNoteEvent() {
    TextNoteEvent textNoteEvent = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_3,
        List.of(new EventTag("TEXT-NOTE-EVENT-3333")),
        CONTENT
    );

    textNoteEvent.setId("333333333333333333");
    eventNotifierEngine.event(new AddNostrEvent<TextNoteEvent>(
        Kind.valueOf(
            textNoteEvent.getKind()
        ),
        Long.valueOf(textNoteEvent.getId()),
        textNoteEvent)
    );
  }

  @Test
  @Order(1)
  void addTwoTextNoteEvents() {
    TextNoteEvent textNoteEvent1 = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_1,
        List.of(new EventTag("TEXT-NOTE-EVENT-11111")),
        CONTENT
    );
    textNoteEvent1.setId("1111111111");
    textNoteEvent1.setKind(Kind.TEXT_NOTE.getValue());

    eventNotifierEngine.event(new AddNostrEvent<TextNoteEvent>(
        Kind.valueOf(
            textNoteEvent1.getKind()
        ),
        Long.valueOf(textNoteEvent1.getId()),
        textNoteEvent1));

    TextNoteEvent textNoteEvent2 = new TextNoteEvent(
        PUB_KEY_TEXTNOTE_2,
        List.of(new EventTag("TEXT-NOTE-EVENT-2222")),
        CONTENT
    );
    textNoteEvent2.setId("2222222222");
    textNoteEvent2.setKind(Kind.TEXT_NOTE.getValue());

    eventNotifierEngine.event(new AddNostrEvent<TextNoteEvent>(
        Kind.valueOf(
            textNoteEvent2.getKind()
        ),
        Long.valueOf(textNoteEvent2.getId()),
        textNoteEvent2)
    );
  }

  @Test
  @Order(4)
  void testTwoClassifiedEvents() {
    ClassifiedListingEvent classifiedEvent1 = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_1,
        List.of(new EventTag("CLASSIFIED-BASE-TAG-11111")),
        CONTENT,
        new ClassifiedListing(
            "classified title 111111",
            "classified summarysummary 11111",
            new PriceTag("1111", "BTC", "1")
        ));
    classifiedEvent1.setId("1111111111");
    classifiedEvent1.setKind(Kind.CLASSIFIED_LISTING.getValue());

    eventNotifierEngine.event(new AddNostrEvent<ClassifiedListingEvent>(
        Kind.valueOf(
            classifiedEvent1.getKind()
        ),
        Long.valueOf(classifiedEvent1.getId()),
        classifiedEvent1)
    );

    ClassifiedListingEvent classifiedEvent2 = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_2,
        List.of(new EventTag("CLASSIFIED-BASE-TAG-2222")),
        CONTENT,
        new ClassifiedListing(
            "classified title 2222",
            "classified summarysummary 22222",
            new PriceTag("2222", "USD", "1")
        ));
    classifiedEvent2.setId("222222222222222");
    classifiedEvent2.setKind(Kind.CLASSIFIED_LISTING.getValue());

    eventNotifierEngine.event(new AddNostrEvent<ClassifiedListingEvent>(
        Kind.valueOf(
            classifiedEvent2.getKind()
        ),
        Long.valueOf(classifiedEvent2.getId()),
        classifiedEvent2)
    );
  }

  @Test
  @Order(3)
  void addSingleClassifiedEvent() {
    ClassifiedListingEvent classifiedEvent = new ClassifiedListingEvent(
        PUB_KEY_CLASSIFIED_3,
        List.of(new EventTag("CLASSIFIED-BASE-TAG-3333")),
        CONTENT,
        new ClassifiedListing(
            "classified title 3333",
            "classified summarysummary 3333",
            new PriceTag("333", "USD", "1")
        ));
    classifiedEvent.setId("33333333333333");
    classifiedEvent.setKind(Kind.CLASSIFIED_LISTING.getValue());

    eventNotifierEngine.event(new AddNostrEvent<ClassifiedListingEvent>(
        Kind.valueOf(
            classifiedEvent.getKind()
        ),
        Long.valueOf(classifiedEvent.getId()),
        classifiedEvent)
    );
  }
}