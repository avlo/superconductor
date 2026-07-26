package com.prosilion.superconductor;

import com.prosilion.nostr.event.AbstractSetsEvent;
import org.junit.jupiter.api.BeforeEach;

public abstract class CacheCuratedServiceTestFixture<T extends AbstractSetsEvent> extends CacheServiceTestFixture<T> {
  T curatedEvent;
  String curatedEventId;

  @BeforeEach
  void setUp() {
    super.setUp();
    this.curatedEvent = super.event;
    this.curatedEventId = super.eventId;
  }
}
