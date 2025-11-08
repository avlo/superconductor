package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.GenericEventRecordDto;
import java.util.List;
import java.util.Optional;

public interface CacheServiceIF {
  BaseEvent save(EventIF event);
  List<? extends BaseEvent> getAll();
  Optional<? extends BaseEvent> getEventByEventId(String eventId);
  List<? extends BaseEvent> getByKind(Kind kind);
  <U extends EventIF> void deleteEvent(U eventIF);

  default Optional<? extends BaseEvent> createBaseEventFromEntityIF(EventIF eventIF) {
    GenericEventRecord genericEventRecord = new GenericEventRecord(
        eventIF.getId(),
        eventIF.getPublicKey(),
        eventIF.getCreatedAt(),
        eventIF.getKind(),
        eventIF.getTags(),
        eventIF.getContent(),
        eventIF.getSignature());
    GenericEventRecordDto genericEventRecordDto = new GenericEventRecordDto(genericEventRecord);
    Optional<? extends BaseEvent> baseEvent = genericEventRecordDto.convertGenericEventRecordToBaseEvent();
    return baseEvent;
  }
}
