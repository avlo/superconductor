package com.prosilion.superconductor.sqlite.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.sqlite.util.Factory;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntity;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventJpaEntityTest {
  public static final String SIGNATURE = Factory.generateRandomHex64String().concat(Factory.generateRandomHex64String());
  public static final String EVENT_ID = Factory.generateRandomHex64String();
  public static final String PUB_KEY = Factory.generateRandomHex64String();
  public static final String CONTENT = Factory.lorumIpsum(EventJpaEntityTest.class);
  public static final Integer KIND = Kind.TEXT_NOTE.getValue();
  public static final long CREATED_AT = System.currentTimeMillis();

  @Test
  void testSettersGetters() {
    EventJpaEntity event = new EventJpaEntity(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        SIGNATURE,
        CONTENT);

    assertEquals(SIGNATURE, event.getSignature().toString());
    assertEquals(EVENT_ID, event.getId());
    assertEquals(PUB_KEY, event.getPublicKey().toString());
    assertEquals(KIND, event.getKind().getValue());
    assertEquals(CREATED_AT, event.getCreatedAt());
    assertEquals(CONTENT, event.getContent());
  }

  @Test
  void testEquals() {
    EventJpaEntity identicalEntity = new EventJpaEntity(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        SIGNATURE,
        CONTENT);

    EventJpaEntity identicalEntity2 = new EventJpaEntity(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        SIGNATURE,
        CONTENT);

    assertEquals(identicalEntity2, identicalEntity);
    assertEquals(identicalEntity.hashCode(), identicalEntity.hashCode());
  }


  @Test
  void canEqual() {
  }

  @Test
  void testToString() {
  }

  @Test
  void getTags() {
    IdentifierTag identifierTag = new IdentifierTag(Factory.generateRandomHex64String());
    PublicKey aTagPubkey = Identity.generateRandomIdentity().getPublicKey();
    AddressTag addressTag = new AddressTag(
        Kind.TEXT_NOTE, aTagPubkey, identifierTag
    );

    List<BaseTag> tags = new ArrayList<>();
    tags.add(new EventTag(Factory.generateRandomHex64String()));
    tags.add(addressTag);

    EventJpaEntity identicalEntity = new EventJpaEntity(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        SIGNATURE,
        CONTENT);
    identicalEntity.setTags(tags);

    EventJpaEntity identicalEntity2 = new EventJpaEntity(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        SIGNATURE,
        CONTENT);
    identicalEntity2.setTags(tags);

    assertEquals(identicalEntity2, identicalEntity);
    assertEquals(identicalEntity.hashCode(), identicalEntity.hashCode());

    List<BaseTag> tags2 = new ArrayList<>();
    tags2.add(new EventTag(Factory.generateRandomHex64String()));
    tags2.add(addressTag);

    EventJpaEntity entity3Diff = new EventJpaEntity(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        SIGNATURE,
        CONTENT);
    entity3Diff.setTags(tags2);

    assertNotEquals(identicalEntity2, entity3Diff);
    assertNotEquals(identicalEntity.hashCode(), entity3Diff.hashCode());

    assertThrows(AssertionFailedError.class, () -> assertEquals(identicalEntity2, entity3Diff));
  }
}
