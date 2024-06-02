package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.repository.EventEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Sql(scripts = {"/data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level
class EventEntityRepositoryTest {
    private static final String SIGNATURE = "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546";

    @Autowired
    EventEntityRepository eventEntityRepository;

//    @BeforeEach
//    void setUp() {
//    }

//    @AfterEach
//    void tearDown() {
//    }

    //    @Sql({"/data.sql"}) // method level
    @Test
    void getId() {
        assertNotNull(eventEntityRepository.findById(1L));
    }

    @Test
    void testRecordNotExist() {
        Optional<EventEntity> eventEntity = eventEntityRepository.findById(0L);
        assertThrows(NoSuchElementException.class, eventEntity::orElseThrow);
    }

    @Test
    void testGetAllFields() {
        Optional<EventEntity> eventEntity = eventEntityRepository.findById(1L);
        assertDoesNotThrow(() -> eventEntity.orElseThrow());
        assertEquals(SIGNATURE, eventEntity.orElseThrow().getSignature());
    }

//    @Test
//    void getEventId() {
//    }
//    @Test
//    void getPubKey() {
//    }
//    @Test
//    void getKind() {
//    }
//    @Test
//    void getNip() {
//    }
//    @Test
//    void getCreatedAt() {
//    }
//    @Test
//    void getContent() {
//    }
//    @Test
//    void getTags() {
//    }
//    @Test
//    void setId() {
//    }
//    @Test
//    void setSignature() {
//    }
//    @Test
//    void setEventId() {
//    }
//    @Test
//    void setPubKey() {
//    }
//    @Test
//    void setKind() {
//    }
//    @Test
//    void setNip() {
//    }
//    @Test
//    void setCreatedAt() {
//    }
//    @Test
//    void setContent() {
//    }
//    @Test
//    void setTags() {
//    }
//    @Test
//    void testEquals() {
//    }
//    @Test
//    void canEqual() {
//    }
//    @Test
//    void testHashCode() {
//    }
//    @Test
//    void testToString() {
//    }
}