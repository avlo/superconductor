package com.prosilion.superconductor.h2db.service;

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
import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.lib.jpa.dto.GenericEventKindDto;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.service.JpaCache;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class JpaCacheIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey EVENT_PUBKEY = IDENTITY.getPublicKey();
  private static final PubKeyTag P_TAG = Factory.createPubKeyTag(IDENTITY);

  private static final EventTag E_TAG = Factory.createEventTag(JpaCacheIT.class);
  private static final GeohashTag G_TAG = Factory.createGeohashTag(JpaCacheIT.class);
  private static final HashtagTag T_TAG = Factory.createHashtagTag(JpaCacheIT.class);
  private static final SubjectTag SUBJECT_TAG = Factory.createSubjectTag(JpaCacheIT.class);
  private static final PriceTag PRICE_TAG = Factory.createPriceTag();

  private final static String CONTENT = Factory.lorumIpsum(JpaCacheIT.class);

  private final JpaCache jpaCache;
  private final TextNoteEvent textNoteEvent;
  private final Long savedId;

  @Autowired
  public JpaCacheIT(JpaCache jpaCache) throws NoSuchAlgorithmException {
    this.jpaCache = jpaCache;

    List<BaseTag> tags = new ArrayList<>();
    tags.add(E_TAG);
    tags.add(P_TAG);
    tags.add(SUBJECT_TAG);
    tags.add(G_TAG);
    tags.add(T_TAG);
    tags.add(PRICE_TAG);

    this.textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.savedId = jpaCache.save(textNoteEvent);
  }

  @Test
  void testGetByEventId() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);

    assertTrue(jpaCache.getAll().stream()
        .map(EventEntityIF::getId)
        .anyMatch(savedId::equals));

    EventEntityIF firstRetrievedEventEntityIF = jpaCache.getEventById(savedId).orElseThrow();
    assertEquals(savedId, firstRetrievedEventEntityIF.getId());

    GenericEventKindDto firstDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF firstEntityIF = firstDto.convertDtoToEntity();
    assertEquals(firstEntityIF, firstRetrievedEventEntityIF);

    EventEntityIF secondRetrievedEntityIF = jpaCache.getEventById(savedId).orElseThrow();
    assertEquals(savedId, secondRetrievedEntityIF.getId());

    GenericEventKindDto secondDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF secondEntityIF = secondDto.convertDtoToEntity();

    assertEquals(secondEntityIF, secondRetrievedEntityIF);
    assertEquals(firstDto, secondDto);
  }

  @Test
  void testGetByEventIdString() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);

    assertTrue(jpaCache.getAll().stream()
        .map(EventEntityIF::getId)
        .anyMatch(savedId::equals));

    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedId);
    log.info("retrieved ids:");
//    all.stream().map(EventEntityIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");

    EventEntityIF firstRetrieval = jpaCache.getByEventIdString(textNoteEvent.getEventId()).orElseThrow();
    assertEquals(savedId, firstRetrieval.getId());

    EventEntityIF secondRetrieval = jpaCache.getByEventIdString(textNoteEvent.getEventId()).orElseThrow();
    assertEquals(savedId, secondRetrieval.getId());

    assertEquals(firstRetrieval, secondRetrieval);

    GenericEventKindDto firstDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF firstEntity = firstDto.convertDtoToEntity();
    assertEquals(firstEntity, firstRetrieval);

    GenericEventKindDto secondDto = new GenericEventKindDto(textNoteEvent);
    EventEntityIF secondEntity = secondDto.convertDtoToEntity();

    assertEquals(firstEntity, secondEntity);
    assertEquals(firstDto, secondDto);
  }
}
