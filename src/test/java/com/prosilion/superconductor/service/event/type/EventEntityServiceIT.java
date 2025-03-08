package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.util.Factory;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.*;
import nostr.id.Identity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EventEntityServiceIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey EVENT_PUBKEY = IDENTITY.getPublicKey();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(EventEntityServiceIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(EventEntityServiceIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(EventEntityServiceIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(EventEntityServiceIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private final static String CONTENT = Factory.lorumIpsum(EventEntityServiceIT.class);
  private final static int KIND = Kind.TEXT_NOTE.getValue();

  @Autowired
  EventEntityService<GenericEvent> eventEntityService;

  private static GenericEvent textNoteEvent;

  @BeforeAll
  static void setUp() {
    List<BaseTag> tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    textNoteEvent = Factory.createTextNoteEvent(IDENTITY, tags, CONTENT);
    IDENTITY.sign(textNoteEvent);
    System.out.println("textNoteEvent getPubKey().toString(): " + textNoteEvent.getPubKey().toString());
    System.out.println("textNoteEvent getPubKey().toHexString(): " + textNoteEvent.getPubKey().toHexString());
    System.out.println("textNoteEvent getPubKey().toBech32String(): " + textNoteEvent.getPubKey().toBech32String());
  }

  @Test
  void saveAndGetEventWithGeohash() {
    Long savedEventId = eventEntityService.saveEventEntity(textNoteEvent);
    GenericEvent savedEvent = eventEntityService.getEventById(savedEventId);

    System.out.println("savedEvent getPubKey().toString(): " + savedEvent.getPubKey().toString());
    System.out.println("savedEvent getPubKey().toHexString(): " + savedEvent.getPubKey().toHexString());
    System.out.println("savedEvent getPubKey().toBech32String(): " + savedEvent.getPubKey().toBech32String());

    assertEquals(CONTENT, savedEvent.getContent());
    assertEquals(KIND, savedEvent.getKind());
    assertEquals(EVENT_PUBKEY.toString(), savedEvent.getPubKey().toString());
    assertEquals(EVENT_PUBKEY.toBech32String(), savedEvent.getPubKey().toBech32String());
    assertEquals(EVENT_PUBKEY.toHexString(), savedEvent.getPubKey().toHexString());

    List<BaseTag> savedEventTags = savedEvent.getTags();
    assertEquals(6, savedEventTags.size());

    assertTrue(savedEventTags.stream().map(BaseTag::getCode).toList()
        .containsAll(
            List.of("e", "g", "p", "t", "price", "subject")));

    assertEquals(E_TAG.getIdEvent(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("e"))
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent).findFirst().orElseThrow());

    assertEquals(G_TAG.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("g"))
        .filter(GeohashTag.class::isInstance)
        .map(GeohashTag.class::cast)
        .map(GeohashTag::toString).findFirst().orElseThrow());

    assertEquals(P_TAG.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("p"))
        .filter(PubKeyTag.class::isInstance)
        .map(PubKeyTag.class::cast)
        .map(PubKeyTag::toString).findFirst().orElseThrow());

    assertEquals(T_TAG.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("t"))
        .filter(HashtagTag.class::isInstance)
        .map(HashtagTag.class::cast)
        .map(HashtagTag::toString).findFirst().orElseThrow());

    assertEquals(PRICE_TAG.getNumber(), PRICE_TAG.getNumber());
    assertEquals(PRICE_TAG.getCurrency(), PRICE_TAG.getCurrency());
    assertEquals(PRICE_TAG.getFrequency(), PRICE_TAG.getFrequency());

    assertEquals(PRICE_TAG.getNumber(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getNumber).findFirst().orElseThrow());

    assertEquals(PRICE_TAG.getCurrency(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getCurrency).findFirst().orElseThrow());

    assertEquals(PRICE_TAG.getFrequency(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getFrequency).findFirst().orElseThrow());
  }
}
