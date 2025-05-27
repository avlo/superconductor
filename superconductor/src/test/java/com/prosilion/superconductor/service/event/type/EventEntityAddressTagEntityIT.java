package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.util.Factory;
import java.util.List;
import lombok.NonNull;
import nostr.base.PublicKey;
import nostr.event.Kind;
import nostr.event.filter.Filterable;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.AddressTag;
import nostr.event.tag.IdentifierTag;
import nostr.id.Identity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class EventEntityAddressTagEntityIT {
  private static final Identity IDENTITY = Factory.createNewIdentity();
  private static final PublicKey ADDRESS_TAG_PUBLIC_KEY = Factory.createNewIdentity().getPublicKey();

  private final static String CONTENT = Factory.lorumIpsum(EventEntityAddressTagEntityIT.class);
  public static final int KIND = Kind.REPUTATION.getValue();
  public static final IdentifierTag IDENTIFIER_TAG = new IdentifierTag(
      "REPUTATION_UUID-needs_proper_attention");

  private final EventEntityService<GenericEvent> eventEntityService;

  private final Long savedEventId;

  @Autowired
  public EventEntityAddressTagEntityIT(@NonNull EventEntityService<GenericEvent> eventEntityService) {
    this.eventEntityService = eventEntityService;

    AddressTag addressTag = new AddressTag(
        KIND,
        ADDRESS_TAG_PUBLIC_KEY,
        IDENTIFIER_TAG);

    GenericEvent textNoteEvent = Factory.createTextNoteEvent(IDENTITY, CONTENT, addressTag);
    IDENTITY.sign(textNoteEvent);
    System.out.println("textNoteEvent getPubKey().toString(): " + textNoteEvent.getPubKey().toString());
    System.out.println("textNoteEvent getPubKey().toHexString(): " + textNoteEvent.getPubKey().toHexString());
    System.out.println("textNoteEvent getPubKey().toBech32String(): " + textNoteEvent.getPubKey().toBech32String());
    savedEventId = eventEntityService.saveEventEntity(textNoteEvent);
  }

  @Test
  void saveAndGetEventWithPublicKey() {
    List<AddressTag> typeSpecificTags = Filterable.getTypeSpecificTags(AddressTag.class, eventEntityService.getEventById(savedEventId));

    assertTrue(typeSpecificTags.stream().anyMatch(tag ->
        tag.getIdentifierTag().getUuid().equals(IDENTIFIER_TAG.getUuid())));
  }
}
