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
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;
import com.prosilion.superconductor.lib.jpa.service.JpaCache;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
  private String eventId;
  private Long savedId;

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

    TextNoteEvent textNoteEvent = new TextNoteEvent(IDENTITY, tags, CONTENT);
    this.eventId = textNoteEvent.getEventId();
    this.savedId = jpaCache.save(textNoteEvent);
  }

  @Test
  @Order(1)
  void testSave() {
    assertNotNull(savedId);
    log.info("saved id: {}", savedId);
  }

  @Test
  @Order(2)
  void testGet() {
    List<EventEntityIF> all = jpaCache.getAll();
    log.info("********************");
    log.info("********************");
    log.info("expicitly saved id: {}", savedId);
    log.info("retrieved ids:");
    all.stream().map(EventEntityIF::getId).forEach(id -> log.info("  {}", id));
    log.info("********************");
    log.info("********************");
    assertTrue(all.stream().map(EventEntityIF::getId).anyMatch(id -> savedId.equals(id)));
  }
}
