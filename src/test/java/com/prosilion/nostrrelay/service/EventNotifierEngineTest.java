package com.prosilion.nostrrelay.service;

import com.prosilion.nostrrelay.pubsub.AddNostrEvent;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.tag.EventTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
class EventNotifierEngineTest {
  private static final PublicKey PUB_KEY_1 = new PublicKey("aaa73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_2 = new PublicKey("bbb73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final PublicKey PUB_KEY_3 = new PublicKey("ccc73464e0688bb3f585f683e57fe1b95e1b47301172ccbe29b30a14ce358c70");
  private static final String CONTENT = "CONTENT";
  private static TextNoteEvent textNoteEvent1;
  private static TextNoteEvent textNoteEvent2;
  private static ClassifiedListingEvent classifiedEvent1;
  private static ClassifiedListingEvent classifiedEvent2;
  private static List<BaseTag> baseTags;
  private static EventNotifierEngine eventNotifierEngine;

  @BeforeAll
  public static void setup() {
    eventNotifierEngine = new EventNotifierEngine();

    baseTags = List.of(new EventTag("EVENT-1"));

    textNoteEvent1 = new TextNoteEvent(
        PUB_KEY_1,
        baseTags,
        CONTENT
    );
    textNoteEvent1.setId("1111111111");
    textNoteEvent1.setKind(Kind.TEXT_NOTE.getValue());

    textNoteEvent2 = new TextNoteEvent(
        PUB_KEY_2,
        baseTags,
        CONTENT
    );
    textNoteEvent2.setId("2222222222");
    textNoteEvent2.setKind(Kind.TEXT_NOTE.getValue());
  }

  @Test()
  @Order(2)
  void addSingleEvent() {
    textNoteEvent1.setId("333333333333333333");
    eventNotifierEngine.event(new AddNostrEvent<TextNoteEvent>(
        Kind.valueOf(
            textNoteEvent1.getKind()
        ),
        Long.valueOf(textNoteEvent1.getId()),
        textNoteEvent1));
  }

  @Test
  @Order(1)
  void addTwoTextNoteEvents() {
    eventNotifierEngine.event(new AddNostrEvent<TextNoteEvent>(
        Kind.valueOf(
            textNoteEvent1.getKind()
        ),
        Long.valueOf(textNoteEvent1.getId()),
        textNoteEvent1));

    eventNotifierEngine.event(new AddNostrEvent<TextNoteEvent>(
        Kind.valueOf(
            textNoteEvent2.getKind()
        ),
        Long.valueOf(textNoteEvent2.getId()),
        textNoteEvent2));
  }

  @Test
  void testEvent() {
  }

  @Test
  void testEvent1() {
  }
}