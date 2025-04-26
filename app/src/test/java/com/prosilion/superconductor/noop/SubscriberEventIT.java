package com.prosilion.superconductor.noop;

import com.prosilion.superconductor.service.event.type.EventEntityService;
import com.prosilion.superconductor.util.Factory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class SubscriberEventIT {
  public static final String EVENT_ID = Factory.generateRandomHex64String();
  public static final PublicKey EVENT_PUBKEY = Factory.createNewIdentity().getPublicKey();
  public static final String CONTENT = Factory.lorumIpsum();
  public static final Integer KIND = 1;
  public static final Integer NIP = 1;
  public static final long CREATED_AT = 1717633851743L;

  @Autowired
  EventEntityService<GenericEvent> eventEntityService;
  ClassifiedListingEvent classifiedListingEvent;

  SubscriberEventIT() {
    GenericEvent genericEvent = new GenericEvent();
    genericEvent.setNip(NIP); // superfluous?
    genericEvent.setId(EVENT_ID);
    genericEvent.setKind(KIND);
    genericEvent.setContent(CONTENT);

    List<BaseTag> tags = new ArrayList<>();
    tags.add(new EventTag(EVENT_ID));
    tags.add(new PubKeyTag(EVENT_PUBKEY));
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
