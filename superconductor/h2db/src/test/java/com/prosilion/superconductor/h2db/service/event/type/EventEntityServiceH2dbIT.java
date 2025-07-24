package com.prosilion.superconductor.h2db.service.event.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.tag.PriceTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.SubjectTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.event.EventEntityService;
import com.prosilion.superconductor.h2db.util.Factory;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class EventEntityServiceH2dbIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey EVENT_PUBKEY = IDENTITY.getPublicKey();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(EventEntityServiceH2dbIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(EventEntityServiceH2dbIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(EventEntityServiceH2dbIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(EventEntityServiceH2dbIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private final static String CONTENT = Factory.lorumIpsum(EventEntityServiceH2dbIT.class);
  private final static Kind KIND = Kind.TEXT_NOTE;

  private final EventEntityService eventEntityService;

  private final BaseEvent textNoteEvent;
  private final Long savedEventId;

  @Autowired
  public EventEntityServiceH2dbIT(@NonNull EventEntityService eventEntityService) throws NostrException, NoSuchAlgorithmException {
    this.eventEntityService = eventEntityService;
    List<BaseTag> tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    savedEventId = eventEntityService.saveEventEntity(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithPublicKey() {
    assertTrue(
        eventEntityService.getEventsByPublicKey(textNoteEvent.getPublicKey())
            .stream().anyMatch(eventEntity ->
                eventEntity.getPublicKey().toHexString().equals(textNoteEvent.getPublicKey().toHexString())));
  }

  @Test
  void saveAndGetEventWithGeohash() {
    GenericEventKindIF savedEvent = eventEntityService.getEventById(savedEventId);

    assertEquals(CONTENT, savedEvent.getContent());
    assertEquals(KIND, savedEvent.getKind());
    assertEquals(EVENT_PUBKEY.toString(), savedEvent.getPublicKey().toString());
    assertEquals(EVENT_PUBKEY.toBech32String(), savedEvent.getPublicKey().toBech32String());
    assertEquals(EVENT_PUBKEY.toHexString(), savedEvent.getPublicKey().toHexString());

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

    assertEquals(0, PRICE_TAG.getNumber().compareTo(savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getNumber).findFirst().orElseThrow()));

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
