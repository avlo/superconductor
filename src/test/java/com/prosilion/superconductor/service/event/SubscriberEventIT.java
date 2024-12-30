package com.prosilion.superconductor.service.event;

import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.ClassifiedListing;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.EventTag;
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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class SubscriberEventIT {
  public static final String EVENT_ID = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  public static final PublicKey EVENT_PUBKEY = new PublicKey("bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984");
  public static final String CONTENT = "1111111111";
  public static final Integer KIND = 1;
  public static final Integer NIP = 1;
  public static final long CREATED_AT = 1717633851743L;

  @Autowired
  EventEntityService<GenericEvent> eventEntityService;
  ClassifiedListingEvent classifiedListingEvent;

  @BeforeAll
  void setUp() {
    GenericEvent genericEvent = new GenericEvent();
    genericEvent.setNip(NIP); // superfluous?
    genericEvent.setId(EVENT_ID);
    genericEvent.setKind(KIND);
    genericEvent.setContent(CONTENT);

    List<BaseTag> tags = new ArrayList<>();
    tags.add(new EventTag("494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"));
    tags.add(new PubKeyTag(new PublicKey("2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")));
    tags.add(new SubjectTag("SUBJECT"));
    genericEvent.setTags(tags);

    genericEvent.setPubKey(EVENT_PUBKEY);
    genericEvent.setCreatedAt(CREATED_AT);
    genericEvent.setSignature(Identity.generateRandomIdentity().sign(genericEvent));

    ClassifiedListing classifiedListing = ClassifiedListing.builder(
            "classified title",
            "classified summary",
            new PriceTag(new BigDecimal(2.71), "BTC", "frequency"))
        .build();

    classifiedListingEvent = new ClassifiedListingEvent(
        genericEvent.getPubKey(),
        genericEvent.getTags(),
        genericEvent.getContent(),
        classifiedListing
    );
    classifiedListingEvent.setId(genericEvent.getId());
    classifiedListingEvent.setCreatedAt(genericEvent.getCreatedAt());
    classifiedListingEvent.setSignature(genericEvent.getSignature());
  }

  @Test
  void saveAndGetEvent() {
//    String newContent = "2222";
//    textNoteEvent.setContent(newContent);
    Long savedEventId = eventEntityService.saveEventEntity(classifiedListingEvent);
    GenericEvent eventDto = eventEntityService.getEventById(savedEventId);
    assertEquals(CONTENT, eventDto.getContent());
    assertEquals(6, eventDto.getTags().size());
  }
}
