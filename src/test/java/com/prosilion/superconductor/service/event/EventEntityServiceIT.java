package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.entity.EventEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class EventEntityServiceIT {
  public static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";
  public static final String EVENT_ID = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  public static final String PUB_KEY = "bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984";
  public static final String CONTENT = "1111111111";
  public static final Integer KIND = 1;
  public static final Integer NIP = 1;
  public static final long CREATED_AT = 1717357053050L;

  @Autowired
  EventEntityService eventEntityService;

  EventEntity event;

  @BeforeAll
  void setUp() {
//    eventEntityService = new EventEntityService();
    event = new EventEntity();
    event.setSignature(SIGNATURE);
    event.setEventId(EVENT_ID);
    event.setPubKey(PUB_KEY);
    event.setKind(KIND);
    event.setNip(NIP);
    event.setCreatedAt(CREATED_AT);
    event.setContent(CONTENT);
  }

  @Test
  void saveEventEntity() {
    assertDoesNotThrow(() -> eventEntityService.saveEventEntity(event.convertEntityToDto()));
  }

  @Test
  void getEvent() {
  }
  @Test
  void getAll() {
  }
  @Test
  void getEventEntityTagEntityService() {
  }
  @Test
  void getEventEntityRepository() {
  }
}