package com.prosilion.superconductor.entity;

import org.junit.jupiter.api.Test;

import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.CONTENT;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.CREATED_AT;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.EVENT_ID;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.KIND;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.NIP;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.PUB_KEY;
import static com.prosilion.superconductor.entity.EventEntityRepositoryIT.SIGNATURE;
import static org.junit.jupiter.api.Assertions.*;

class EventEntityTest {
  EventEntity event = new EventEntity();

  @Test
  void testSettersGetters() {
    event.setSignature(SIGNATURE);
    event.setEventId(EVENT_ID);
    event.setPubKey(PUB_KEY);
    event.setKind(KIND);
    event.setNip(NIP);
    event.setCreatedAt(CREATED_AT);
    event.setContent(CONTENT);

    assertEquals(SIGNATURE, event.getSignature());
    assertEquals(EVENT_ID, event.getEventId());
    assertEquals(PUB_KEY, event.getPubKey());
    assertEquals(KIND, event.getKind());
    assertEquals(NIP, event.getNip());
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