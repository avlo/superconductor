package com.prosilion.superconductor.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventEntityTest {
  public static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";
  public static final String EVENT_ID = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  public static final String PUB_KEY = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
  public static final String CONTENT = "1111111111";
  public static final Integer KIND = 1;
  public static final long CREATED_AT = 1717357053050L;
  EventEntity event = new EventEntity();

  @Test
  void testSettersGetters() {
    event.setSignature(SIGNATURE);
    event.setEventIdString(EVENT_ID);
    event.setPubKey(PUB_KEY);
    event.setKind(KIND);
    event.setCreatedAt(CREATED_AT);
    event.setContent(CONTENT);

    assertEquals(SIGNATURE, event.getSignature());
    assertEquals(EVENT_ID, event.getEventIdString());
    assertEquals(PUB_KEY, event.getPubKey());
    assertEquals(KIND, event.getKind());
    assertEquals(CREATED_AT, event.getCreatedAt());
    assertEquals(CONTENT, event.getContent());
  }

  @Test
  void testEquals() {
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
