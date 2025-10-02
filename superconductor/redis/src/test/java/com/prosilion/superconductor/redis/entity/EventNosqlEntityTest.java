package com.prosilion.superconductor.redis.entity;

import com.prosilion.superconductor.lib.redis.document.EventNosqlEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventNosqlEntityTest {
  public static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";
  public static final String EVENT_ID = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  public static final String PUB_KEY = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
  public static final String CONTENT = "1111111111";
  public static final Integer KIND = 1;
  public static final long CREATED_AT = 1717357053050L;

  @Test
  void testEventNosqlEntityOf() {
    EventNosqlEntity eventNosqlEntity = EventNosqlEntity.of(
        EVENT_ID,
        KIND,
        PUB_KEY,
        CREATED_AT,
        CONTENT,
        SIGNATURE);

    assertEquals(SIGNATURE, eventNosqlEntity.getSignature().toString());
    assertEquals(EVENT_ID, eventNosqlEntity.getId());
    assertEquals(PUB_KEY, eventNosqlEntity.getPubKey());
    assertEquals(KIND, eventNosqlEntity.getKind().getValue());
    assertEquals(CREATED_AT, eventNosqlEntity.getCreatedAt());
    assertEquals(CONTENT, eventNosqlEntity.getContent());
  }

  @Test
  void canEqual() {
  }

  @Test
  void testHashCode() {
  }

  @Test
  void testToString() {
  }

  @Test
  void getTags() {
  }
}
