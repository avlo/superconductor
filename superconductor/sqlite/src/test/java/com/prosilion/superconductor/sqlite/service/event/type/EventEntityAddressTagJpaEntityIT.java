package com.prosilion.superconductor.sqlite.service.event.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.service.EventJpaEntityService;
import com.prosilion.superconductor.sqlite.util.Factory;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class EventEntityAddressTagJpaEntityIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey ADDRESS_TAG_PUBLIC_KEY = Factory.createNewIdentity().getPublicKey();

  private final static String CONTENT = Factory.lorumIpsum(EventEntityAddressTagJpaEntityIT.class);
  public static final Kind KIND = Kind.BADGE_AWARD_EVENT;
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(
      "REPUTATION_UUID-needs_proper_attention");

  private final EventJpaEntityService eventJpaEntityService;

  private final Long savedEventId;

  @Autowired
  public EventEntityAddressTagJpaEntityIT(@NonNull EventJpaEntityService eventJpaEntityService) throws NostrException, NoSuchAlgorithmException {
    this.eventJpaEntityService = eventJpaEntityService;

    AddressTag addressTag = new AddressTag(
        KIND,
        ADDRESS_TAG_PUBLIC_KEY,
        IDENTIFIER_TAG);


    EventIF textNoteEvent = new TextNoteEvent(IDENTITY, List.of(addressTag), CONTENT);
    System.out.println("textNoteEvent getPubKey().toString(): " + textNoteEvent.getPublicKey().toString());
    System.out.println("textNoteEvent getPubKey().toHexString(): " + textNoteEvent.getPublicKey().toHexString());
    System.out.println("textNoteEvent getPubKey().toBech32String(): " + textNoteEvent.getPublicKey().toBech32String());
    savedEventId = eventJpaEntityService.saveEvent(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithPublicKey() {
    List<AddressTag> typeSpecificTags = Filterable.getTypeSpecificTags(AddressTag.class, eventJpaEntityService.getEventByUid(savedEventId).orElseThrow());

    assertTrue(typeSpecificTags.stream().anyMatch(tag ->
        tag.getIdentifierTag().getUuid().equals(IDENTIFIER_TAG.getUuid())));
  }
}
