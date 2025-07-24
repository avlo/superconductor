//package com.prosilion.superconductor.noop;
//
//import com.prosilion.nostr.event.ClassifiedListingEvent;
//import com.prosilion.nostr.event.internal.ClassifiedListing;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.superconductor.base.type.event.service.EventEntityService;
//import com.prosilion.superconductor.sqlite.util.Factory;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.event.GenericEventKindIF;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.PriceTag;
//import com.prosilion.nostr.tag.PubKeyTag;
//import com.prosilion.nostr.tag.SubjectTag;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("test")
//class SubscriberNoOpEventIT {
//  public static final String EVENT_ID = Factory.generateRandomHex64String();
//  public static final PublicKey EVENT_PUBKEY = Factory.createNewIdentity().getPublicKey();
//  public static final String CONTENT = Factory.lorumIpsum();
//  public static final Integer KIND = 1;
//  public static final long CREATED_AT = 1717633851743L;
//
//  @Autowired
//  EventEntityService<GenericEventKindIF> eventEntityService;
//  ClassifiedListingEvent classifiedListingEvent;
//
//  SubscriberNoOpEventIT() {
//    GenericEventKindIF genericEvent = new GenericEventKindIF();
//    genericEvent.setId(EVENT_ID);
//    genericEvent.setKind(KIND);
//    genericEvent.setContent(CONTENT);
//
//    List<BaseTag> tags = new ArrayList<>();
//    tags.add(new EventTag(EVENT_ID));
//    tags.add(new PubKeyTag(EVENT_PUBKEY));
//    tags.add(new SubjectTag("SUBJECT"));
//
//    genericEvent.setPubKey(EVENT_PUBKEY);
//    genericEvent.setCreatedAt(CREATED_AT);
//    genericEvent.setSignature(Identity.generateRandomIdentity().sign(genericEvent));
//
//    ClassifiedListing classifiedListing = ClassifiedListing.builder(
//            "classified title",
//            "classified summary",
//            new PriceTag(new BigDecimal(2.71), "BTC", "frequency"))
//        .build();
//
//    classifiedListingEvent = new ClassifiedListingEvent(
//        genericEvent.getPubKey(),
//        genericEvent.getTags(),
//        genericEvent.getContent(),
//        classifiedListing
//    );
//    classifiedListingEvent.setId(genericEvent.getId());
//    classifiedListingEvent.setCreatedAt(genericEvent.getCreatedAt());
//    classifiedListingEvent.setSignature(genericEvent.getSignature());
//  }
//
//  @Test
//  void saveAndGetEvent() {
////    String newContent = "2222";
////    textNoteEvent.setContent(newContent);
//    Long savedEventId = eventEntityService.saveEventEntity(classifiedListingEvent);
//    GenericEventKindIF eventDto = eventEntityService.getEventById(savedEventId);
//    assertEquals(CONTENT, eventDto.getContent());
//    assertEquals(6, eventDto.getTags().size());
//  }
//}
