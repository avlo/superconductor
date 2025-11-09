package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeAwardAbstractEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import org.springframework.lang.NonNull;

public class BadgeAwardGenericEventRecordRedisEvent extends BadgeAwardAbstractEvent {
  public BadgeAwardGenericEventRecordRedisEvent(@NonNull GenericEventRecord genericEventRecord) {
    super(genericEventRecord);
  }
}
