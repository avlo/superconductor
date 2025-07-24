package com.prosilion.superconductor.sqlite.service.event.type;

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
import com.prosilion.superconductor.sqlite.util.Factory;
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
class EventEntityServiceSqliteIT {
  private final PublicKey event_pubkey;
  private final PubKeyTag p_tag;
  private final EventTag e_tag;
  private final GeohashTag g_tag;
  private final HashtagTag t_tag;
  private final PriceTag price_tag;
  private final String content;
  private final static Kind KIND = Kind.TEXT_NOTE;

  private final EventEntityService eventEntityService;
  private final BaseEvent textNoteEvent;
  private final Long savedEventId;

  @Autowired
  public EventEntityServiceSqliteIT(@NonNull EventEntityService eventEntityService) throws NostrException, NoSuchAlgorithmException {
    Identity identity = Factory.createNewIdentity();
    event_pubkey = identity.getPublicKey();
    p_tag = Factory.createPubKeyTag(identity);
    e_tag = Factory.createEventTag(EventEntityServiceSqliteIT.class);
    g_tag = Factory.createGeohashTag(EventEntityServiceSqliteIT.class);
    t_tag = Factory.createHashtagTag(EventEntityServiceSqliteIT.class);
    SubjectTag subject_tag = Factory.createSubjectTag(EventEntityServiceSqliteIT.class);
    price_tag = Factory.createPriceTag();

    this.eventEntityService = eventEntityService;
    List<BaseTag> tags = new ArrayList<>();
    tags.add(e_tag);
    tags.add(p_tag);
    tags.add(subject_tag);
    tags.add(g_tag);
    tags.add(t_tag);
    tags.add(price_tag);

    content = Factory.lorumIpsum(EventEntityServiceSqliteIT.class);

    textNoteEvent = new TextNoteEvent(identity, tags, content);
    savedEventId = eventEntityService.saveEventEntity(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithPublicKey() {
    List<GenericEventKindIF> eventsByPublicKey = eventEntityService.getEventsByPublicKey(textNoteEvent.getPublicKey());
    assertTrue(
        eventsByPublicKey
            .stream().anyMatch(eventEntity ->
                eventEntity.getPublicKey().toHexString().equals(textNoteEvent.getPublicKey().toHexString())));
  }

  @Test
  void saveAndGetEventWithGeohash() {
    GenericEventKindIF savedEvent = eventEntityService.getEventById(savedEventId);

    assertEquals(content, savedEvent.getContent());
    assertEquals(KIND, savedEvent.getKind());
    assertEquals(event_pubkey.toString(), savedEvent.getPublicKey().toString());
    assertEquals(event_pubkey.toBech32String(), savedEvent.getPublicKey().toBech32String());
    assertEquals(event_pubkey.toHexString(), savedEvent.getPublicKey().toHexString());

    List<BaseTag> savedEventTags = savedEvent.getTags();
    assertEquals(6, savedEventTags.size());

    assertTrue(savedEventTags.stream().map(BaseTag::getCode).toList()
        .containsAll(
            List.of("e", "g", "p", "t", "price", "subject")));

    assertEquals(e_tag.getIdEvent(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("e"))
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent).findFirst().orElseThrow());

    assertEquals(g_tag.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("g"))
        .filter(GeohashTag.class::isInstance)
        .map(GeohashTag.class::cast)
        .map(GeohashTag::toString).findFirst().orElseThrow());

    assertEquals(p_tag.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("p"))
        .filter(PubKeyTag.class::isInstance)
        .map(PubKeyTag.class::cast)
        .map(PubKeyTag::toString).findFirst().orElseThrow());

    assertEquals(t_tag.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("t"))
        .filter(HashtagTag.class::isInstance)
        .map(HashtagTag.class::cast)
        .map(HashtagTag::toString).findFirst().orElseThrow());

    assertEquals(0, price_tag.getNumber().compareTo(savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getNumber).findFirst().orElseThrow()));

    assertEquals(price_tag.getCurrency(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getCurrency).findFirst().orElseThrow());

    assertEquals(price_tag.getFrequency(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getFrequency).findFirst().orElseThrow());
  }
}
