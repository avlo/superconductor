package com.prosilion.superconductor.service.event;

import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.GeohashTag;
import nostr.event.tag.HashtagTag;
import nostr.event.tag.PriceTag;
import nostr.event.tag.PubKeyTag;
import nostr.event.tag.SubjectTag;
import nostr.id.Identity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class ClassifiedListingEventIT {
  public static final String EVENT_HEX = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
  public static final String PTAG_HEX = "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76985";
  public static final String ETAG_HEX = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4347";

  public static final PublicKey EVENT_PUBKEY = new PublicKey(EVENT_HEX);
  public static final PubKeyTag P_TAG = new PubKeyTag(new PublicKey(PTAG_HEX));
  public static final EventTag E_TAG = new EventTag(ETAG_HEX);

  public static final String SUBJECT = "Classified Listing Test Subject Tag";
  public static final SubjectTag SUBJECT_TAG = new SubjectTag(SUBJECT);
  public static final GeohashTag G_TAG = new GeohashTag("Classified Listing Test Geohash Tag");
  public static final HashtagTag T_TAG = new HashtagTag("Classified Listing Test Hashtag Tag");

  public static final BigDecimal NUMBER = new BigDecimal("2.71");
  public static final String FREQUENCY = "NANOSECOND";
  public static final String CURRENCY = "BTC";
  public static final PriceTag PRICE_TAG = new PriceTag(NUMBER, CURRENCY, FREQUENCY);

  public static final Integer CLASSIFIED_LISTING_KIND = 30402;
  public static final String CONTENT = "Test Content";
  public static final String CLASSIFIED_LISTING_TITLE = "classified listing title";
  public static final String CLASSIFIED_LISTING_SUMMARY = "classified listing summary";
  public static final String CLASSIFIED_LISTING_PUBLISHED_AT = "1687765220";
  public static final String CLASSIFIED_LISTING_LOCATION = "classified listing location";
  public static final String TITLE_CODE = "title";
  public static final String SUMMARY_CODE = "summary";
  public static final String PUBLISHED_AT_CODE = "published_at";
  public static final String LOCATION_CODE = "location";

  @Autowired
  EventEntityService<GenericEvent> eventEntityService;

  GenericEvent classifiedListingEvent;

  @BeforeAll
  void setUp() {
    classifiedListingEvent = new GenericEvent();

    classifiedListingEvent.setKind(CLASSIFIED_LISTING_KIND);
    classifiedListingEvent.setContent(CONTENT);

    List<BaseTag> tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(GenericTag.create(TITLE_CODE, 99, CLASSIFIED_LISTING_TITLE));
    tags.add(GenericTag.create(SUMMARY_CODE, 99, CLASSIFIED_LISTING_SUMMARY));
    tags.add(GenericTag.create(PUBLISHED_AT_CODE, 99, CLASSIFIED_LISTING_PUBLISHED_AT));
    tags.add(GenericTag.create(LOCATION_CODE, 99, CLASSIFIED_LISTING_LOCATION));
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);
    classifiedListingEvent.setTags(tags);

    classifiedListingEvent.setPubKey(EVENT_PUBKEY);
    classifiedListingEvent.setSignature(Identity.generateRandomIdentity().sign(classifiedListingEvent));
  }

  @Test
  void saveAndGetClassifiedListingEvent() {
    Long savedEventId = eventEntityService.saveEventEntity(classifiedListingEvent);
    GenericEvent savedEvent = eventEntityService.getEventById(savedEventId);

    assertEquals(CONTENT, savedEvent.getContent());
    assertEquals(CLASSIFIED_LISTING_KIND, savedEvent.getKind());
    assertEquals(EVENT_PUBKEY.toString(), savedEvent.getPubKey().toString());
    assertEquals(EVENT_PUBKEY.toBech32String(), savedEvent.getPubKey().toBech32String());
    assertEquals(EVENT_PUBKEY.toHexString(), savedEvent.getPubKey().toHexString());

    List<BaseTag> savedEventTags = savedEvent.getTags();
    assertEquals(10, savedEventTags.size());

    assertTrue(savedEventTags.stream().map(BaseTag::getCode).toList()
        .containsAll(
            List.of(TITLE_CODE, SUMMARY_CODE, PUBLISHED_AT_CODE, LOCATION_CODE)));

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

    assertEquals(NUMBER, PRICE_TAG.getNumber());
    assertEquals(CURRENCY, PRICE_TAG.getCurrency());
    assertEquals(FREQUENCY, PRICE_TAG.getFrequency());
    assertEquals(SUBJECT, SUBJECT_TAG.getSubject());

    assertTrue(genericTagExists(savedEventTags.stream(), TITLE_CODE, CLASSIFIED_LISTING_TITLE));
    assertTrue(genericTagExists(savedEventTags.stream(), SUMMARY_CODE, CLASSIFIED_LISTING_SUMMARY));
    assertTrue(genericTagExists(savedEventTags.stream(), PUBLISHED_AT_CODE, CLASSIFIED_LISTING_PUBLISHED_AT));
    assertTrue(genericTagExists(savedEventTags.stream(), LOCATION_CODE, CLASSIFIED_LISTING_LOCATION));

    assertEquals(SUBJECT, savedEventTags.stream().filter(subjectTag ->
            subjectTag.getCode().equalsIgnoreCase("subject"))
        .filter(SubjectTag.class::isInstance)
        .map(SubjectTag.class::cast)
        .map(SubjectTag::getSubject).findFirst().orElseThrow());

    assertEquals(NUMBER, savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getNumber).findFirst().orElseThrow());

    assertEquals(CURRENCY, savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getCurrency).findFirst().orElseThrow());

    assertEquals(FREQUENCY, savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getFrequency).findFirst().orElseThrow());
  }

  private static boolean genericTagExists(Stream<BaseTag> savedEventTags, String code, String value) {
    return savedEventTags.filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase(code))
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .map(GenericTag::getAttributes)
        .map(Collection::stream)
        .anyMatch(elementAttributeStream ->
            elementAttributeStream.anyMatch(elementAttribute ->
                elementAttribute.getValue().equals(value)));
  }
}
