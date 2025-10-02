package com.prosilion.superconductor.redis.service.event.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
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
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.service.EventNosqlEntityService;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
class EventNosqlEntityServiceIT {
  private final PublicKey event_pubkey;
  private final PubKeyTag p_tag;
  private final EventTag e_tag;
  private final GeohashTag g_tag;
  private final HashtagTag t_tag;
  private final PriceTag price_tag;
  private final String content;
  private final static Kind KIND = Kind.TEXT_NOTE;

  private final EventNosqlEntityService eventNosqlEntityService;
  private final EventNosqlEntityIF eventNosqlEntity;

  @Autowired
  public EventNosqlEntityServiceIT(@NonNull EventNosqlEntityService eventNosqlEntityService) throws NostrException, NoSuchAlgorithmException {
    Identity identity = Factory.createNewIdentity();
    event_pubkey = identity.getPublicKey();
    p_tag = Factory.createPubKeyTag(identity);
    e_tag = Factory.createEventTag(EventNosqlEntityServiceIT.class);
    g_tag = Factory.createGeohashTag(EventNosqlEntityServiceIT.class);
    t_tag = Factory.createHashtagTag(EventNosqlEntityServiceIT.class);
    SubjectTag subject_tag = Factory.createSubjectTag(EventNosqlEntityServiceIT.class);
    price_tag = Factory.createPriceTag();

    this.eventNosqlEntityService = eventNosqlEntityService;
    List<BaseTag> tags = new ArrayList<>();
    tags.add(e_tag);
    tags.add(p_tag);
    tags.add(subject_tag);
    tags.add(g_tag);
    tags.add(t_tag);
    tags.add(price_tag);

    content = Factory.lorumIpsum(EventNosqlEntityServiceIT.class);

    EventIF textNoteEvent = new TextNoteEvent(identity, tags, content);
    eventNosqlEntity = this.eventNosqlEntityService.saveEvent(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithGeohash() {
    EventNosqlEntityIF savedEvent = eventNosqlEntityService.findByEventIdString(eventNosqlEntity.getId()).orElseThrow();
    log.debug("savedEvent getPubKey().toString(): " + savedEvent.getPublicKey().toString());
    log.debug("savedEvent getPubKey().toHexString(): " + savedEvent.getPublicKey().toHexString());
    log.debug("savedEvent getPubKey().toBech32String(): " + savedEvent.getPublicKey().toBech32String());

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

    assertEquals(t_tag.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("t"))
        .filter(HashtagTag.class::isInstance)
        .map(HashtagTag.class::cast)
        .map(HashtagTag::toString).findFirst().orElseThrow());

    assertEquals(price_tag.getNumber(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("price"))
        .filter(PriceTag.class::isInstance)
        .map(PriceTag.class::cast)
        .map(PriceTag::getNumber).findFirst().orElseThrow());

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

    assertEquals(p_tag.toString(), savedEventTags.stream().filter(baseTag ->
            baseTag.getCode().equalsIgnoreCase("p"))
        .filter(PubKeyTag.class::isInstance)
        .map(PubKeyTag.class::cast)
        .map(PubKeyTag::toString).findFirst().orElseThrow());
  }
}

