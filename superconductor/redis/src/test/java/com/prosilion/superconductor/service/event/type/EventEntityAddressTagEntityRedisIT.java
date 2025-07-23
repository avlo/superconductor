package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.service.EventDocumentService;
import com.prosilion.superconductor.util.Factory;
import io.github.tobi.laa.spring.boot.embedded.redis.standalone.EmbeddedRedisStandalone;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@EmbeddedRedisStandalone
@SpringBootTest
@ActiveProfiles("test")
class EventEntityAddressTagEntityRedisIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey ADDRESS_TAG_PUBLIC_KEY = Factory.createNewIdentity().getPublicKey();

  private final static String CONTENT = Factory.lorumIpsum(EventEntityAddressTagEntityRedisIT.class);
  public static final Kind KIND = Kind.BADGE_AWARD_EVENT;
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(
      "REPUTATION_UUID-needs_proper_attention");

  private final EventDocumentService<BaseTag> eventDocumentService;

  private final EventDocument savedEventDocument;

  @Autowired
  public EventEntityAddressTagEntityRedisIT(@NonNull EventDocumentService<BaseTag> eventDocumentService) throws NostrException, NoSuchAlgorithmException {
    this.eventDocumentService = eventDocumentService;

    AddressTag addressTag = new AddressTag(
        KIND,
        ADDRESS_TAG_PUBLIC_KEY,
        IDENTIFIER_TAG);


    BaseEvent textNoteEvent = new TextNoteEvent(IDENTITY, List.of(addressTag), CONTENT);
    System.out.println("textNoteEvent getPubKey().toString(): " + textNoteEvent.getPublicKey().toString());
    System.out.println("textNoteEvent getPubKey().toHexString(): " + textNoteEvent.getPublicKey().toHexString());
    System.out.println("textNoteEvent getPubKey().toBech32String(): " + textNoteEvent.getPublicKey().toBech32String());
    savedEventDocument = eventDocumentService.saveEventDocument(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithPublicKey() {
    GenericEventKindIF genericEventKindIF = eventDocumentService.findByEventIdString(savedEventDocument.getEventIdString()).orElseThrow();

    List<BaseTag> tags = genericEventKindIF.getTags();
    tags.forEach(tag -> log.debug("\ntag:  \n{}\n ---- \n", tag));

    assertTrue(tags.stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast)
        .anyMatch(tag ->
            tag.getIdentifierTag().getUuid().equals(IDENTIFIER_TAG.getUuid())));
  }
}
