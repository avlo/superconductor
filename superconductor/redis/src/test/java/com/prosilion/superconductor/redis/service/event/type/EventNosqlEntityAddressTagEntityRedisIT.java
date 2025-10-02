package com.prosilion.superconductor.redis.service.event.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.service.EventNosqlEntityService;
import com.prosilion.superconductor.redis.util.Factory;
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
class EventNosqlEntityAddressTagEntityRedisIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey ADDRESS_TAG_PUBLIC_KEY = Factory.createNewIdentity().getPublicKey();

  private final static String CONTENT = Factory.lorumIpsum(EventNosqlEntityAddressTagEntityRedisIT.class);
  public static final Kind KIND = Kind.BADGE_AWARD_EVENT;
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(
      "REPUTATION_UUID-needs_proper_attention");

  private final EventNosqlEntityService eventNosqlEntityService;

  private final EventNosqlEntityIF eventNosqlEntity;

  @Autowired
  public EventNosqlEntityAddressTagEntityRedisIT(@NonNull EventNosqlEntityService eventNosqlEntityService) throws NostrException, NoSuchAlgorithmException {
    this.eventNosqlEntityService = eventNosqlEntityService;

    AddressTag addressTag = new AddressTag(
        KIND,
        ADDRESS_TAG_PUBLIC_KEY,
        IDENTIFIER_TAG);


    EventIF textNoteEvent = new TextNoteEvent(IDENTITY, List.of(addressTag), CONTENT);
    System.out.println("textNoteEvent getPubKey().toString(): " + textNoteEvent.getPublicKey().toString());
    System.out.println("textNoteEvent getPubKey().toHexString(): " + textNoteEvent.getPublicKey().toHexString());
    System.out.println("textNoteEvent getPubKey().toBech32String(): " + textNoteEvent.getPublicKey().toBech32String());
    eventNosqlEntity = eventNosqlEntityService.saveEvent(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithPublicKey() {
    EventNosqlEntityIF genericEventKindIF = eventNosqlEntityService.findByEventIdString(eventNosqlEntity.getId()).orElseThrow();

    List<BaseTag> tags = genericEventKindIF.getTags();
    tags.forEach(tag -> log.debug("\ntag:  \n{}\n ---- \n", tag));

    assertTrue(tags.stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast)
        .anyMatch(tag ->
            tag.getIdentifierTag().getUuid().equals(IDENTIFIER_TAG.getUuid())));
  }
}
