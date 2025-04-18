package com.prosilion.superconductor.service.event.type;

import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.GenericTag;
import nostr.event.tag.*;
import nostr.id.Identity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class ZapRequestEventIT {
  public static final String EVENT_HEX = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
  public static final String PTAG_HEX = "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76985";
  public static final String ETAG_HEX = "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4347";

  public static final PublicKey EVENT_PUBKEY = new PublicKey(EVENT_HEX);
  public static final PubKeyTag P_TAG = new PubKeyTag(new PublicKey(PTAG_HEX));
  public static final EventTag E_TAG = new EventTag(ETAG_HEX);

  public static final String SUBJECT = "Zap Request Test Subject Tag";
  public static final SubjectTag SUBJECT_TAG = new SubjectTag(SUBJECT);
  public static final GeohashTag G_TAG = new GeohashTag("Zap Request Test Geohash Tag");
  public static final HashtagTag T_TAG = new HashtagTag("Zap Request Test Hashtag Tag");

  public static final String AMOUNT_CODE = "amount";
  public static final String AMOUNT = "1111";
  public static final String LNURL_CODE = "lnurl";
  public static final String LN_URL = "lnurl1dp68gurn8ghj7ar0wfsj6er9wchxuemjda4ju6t09ashq6f0w4ek2u30d3h82unv8a6xzeead3hkw6twye4nz0fcxgmnsef3vy6rsefkx93nyd338ycnvdeex9jxzcnzxeskvdekxq6rswr9x3nrqvfexvex2vf3vejnwvp4x3nr2wfhx56x2vmyv5mx2udztdn";

  public static final Integer ZAP_REQUEST_KIND = 9734;
  public static final String CONTENT = "Zap Request Content";

  private final RelaysTag relaysTag;
  private final String websocketUrl;

  @Autowired
  EventEntityService<GenericEvent> eventEntityService;

  GenericEvent zapRequestEvent;

  public ZapRequestEventIT(@Value("${superconductor.relay.url}") String relayUri) {
    this.websocketUrl = relayUri;
    relaysTag = new RelaysTag(new Relay(relayUri));

    zapRequestEvent = new GenericEvent();

    zapRequestEvent.setKind(ZAP_REQUEST_KIND);
    zapRequestEvent.setContent(CONTENT);

    List<BaseTag> tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(GenericTag.create(AMOUNT_CODE, 57, AMOUNT));
    tags.add(GenericTag.create(LNURL_CODE, 57, LN_URL));
    tags.add(relaysTag);
    zapRequestEvent.setTags(tags);

    zapRequestEvent.setPubKey(EVENT_PUBKEY);
    zapRequestEvent.setSignature(Identity.generateRandomIdentity().sign(zapRequestEvent));
  }

  @Test
  void saveAndGetZapRequestEvent() {
    Long savedEventId = eventEntityService.saveEventEntity(zapRequestEvent);
    GenericEvent savedEvent = eventEntityService.getEventById(savedEventId);

    assertEquals(CONTENT, savedEvent.getContent());
    assertEquals(ZAP_REQUEST_KIND, savedEvent.getKind());
    assertEquals(EVENT_PUBKEY.toString(), savedEvent.getPubKey().toString());
    assertEquals(EVENT_PUBKEY.toBech32String(), savedEvent.getPubKey().toBech32String());
    assertEquals(EVENT_PUBKEY.toHexString(), savedEvent.getPubKey().toHexString());

    List<BaseTag> savedEventTags = savedEvent.getTags();
    assertEquals(8, savedEventTags.size());

    assertTrue(savedEventTags.stream().map(BaseTag::getCode).toList()
        .containsAll(
            List.of(AMOUNT_CODE, LNURL_CODE)));

    assertTrue(savedEventTags.stream().map(BaseTag::getCode).toList()
        .containsAll(
            List.of("e", "g", "p", "t", "relays", "subject")));

    assertTrue(genericTagExists(savedEventTags.stream(), AMOUNT_CODE, AMOUNT));
    assertTrue(genericTagExists(savedEventTags.stream(), LNURL_CODE, LN_URL));

    assertEquals(E_TAG.getIdEvent(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("e"))
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent).findFirst().orElseThrow());

    assertEquals(P_TAG.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("p"))
        .filter(PubKeyTag.class::isInstance)
        .map(PubKeyTag.class::cast)
        .map(PubKeyTag::toString).findFirst().orElseThrow());

    assertEquals(G_TAG.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("g"))
        .filter(GeohashTag.class::isInstance)
        .map(GeohashTag.class::cast)
        .map(GeohashTag::toString).findFirst().orElseThrow());

    assertEquals(T_TAG.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("t"))
        .filter(HashtagTag.class::isInstance)
        .map(HashtagTag.class::cast)
        .map(HashtagTag::toString).findFirst().orElseThrow());

    assertEquals(SUBJECT, SUBJECT_TAG.getSubject());

    assertEquals(SUBJECT, savedEventTags.stream().filter(subjectTag ->
            subjectTag.getCode().equalsIgnoreCase("subject"))
        .filter(SubjectTag.class::isInstance)
        .map(SubjectTag.class::cast)
        .map(SubjectTag::getSubject).findFirst().orElseThrow());

// list contains a relay
    assertDoesNotThrow(() -> savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("relays"))
        .filter(RelaysTag.class::isInstance)
        .map(RelaysTag.class::cast)
        .map(RelaysTag::getRelays).findFirst().orElseThrow());

// setup test on line 146
    List<Relay> relays = savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("relays"))
        .filter(RelaysTag.class::isInstance)
        .map(RelaysTag.class::cast)
        .map(RelaysTag::getRelays).findFirst().orElseThrow();

    assertTrue(relays.stream().map(Relay::getUri).toList().contains(websocketUrl));
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
