package com.prosilion.superconductor.redis.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.ClassifiedListingEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.internal.ClassifiedListing;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.PriceTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.SubjectTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.service.EventDocumentService;
import com.prosilion.superconductor.redis.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.math.BigDecimal;
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

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
class SubscriberEventRedisIT {
  public static final Identity IDENTITY = Factory.createNewIdentity();
  public static final String CONTENT = Factory.lorumIpsum(SubscriberEventRedisIT.class);

  private final EventDocumentService<BaseTag> eventDocumentService;

  ClassifiedListingEvent classifiedListingEvent;

  @Autowired
  public SubscriberEventRedisIT(@NonNull EventDocumentService<BaseTag> eventDocumentService) throws NoSuchAlgorithmException {
    this.eventDocumentService = eventDocumentService;

    List<BaseTag> tags = new ArrayList<>();
    tags.add(new EventTag("494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"));
    tags.add(new PubKeyTag(new PublicKey("2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")));
    tags.add(new SubjectTag("SUBJECT"));

    ClassifiedListing classifiedListing = new ClassifiedListing(
        "classified title",
        "classified summary",
        new PriceTag(new BigDecimal("2.71"), "BTC", "frequency"));

    classifiedListingEvent = new ClassifiedListingEvent(IDENTITY, Kind.CLASSIFIED_LISTING, classifiedListing, tags, CONTENT);
  }

  @Test
  void saveAndGetEvent() {
    EventDocument eventDocument = eventDocumentService.saveEventDocument(classifiedListingEvent);
    GenericEventKindIF foundEvent = eventDocumentService.findByEventIdString(eventDocument.getEventIdString()).orElseThrow();
    assertEquals(CONTENT, foundEvent.getContent());
    List<BaseTag> tags = foundEvent.getTags();
    tags.forEach(tag -> log.debug("\ntag:  \n{}\n ---- \n", tag));
    assertEquals(4, tags.size());
  }
}
