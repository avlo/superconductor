package com.prosilion.nostrrelay.dto.event;

import com.prosilion.nostrrelay.entity.EventEntity;
import com.prosilion.nostrrelay.entity.EventEntityDecorator;
import nostr.base.PublicKey;
import nostr.base.Signature;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.NIP01Event;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
public class EventDto extends NIP01Event {
  private final EventEntityDecorator eventEntityDecorator;

  public EventDto(PublicKey pubKey, String eventId, Kind kind, Integer nip, Long createdAt, Signature signature, List<BaseTag> tags, EventEntityDecorator eventEntityDecorator) {
    super(pubKey, kind, tags);
    setId(eventId);
    setNip(nip);
    setCreatedAt(createdAt);
    setSignature(signature);
    this.eventEntityDecorator = eventEntityDecorator;
  }

  public EventEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    EventEntity eventEntity = new EventEntity(getId(), getKind(), getNip(), getPubKey().toString(), getCreatedAt(), getSignature().toString(), eventEntityDecorator);
    BeanUtils.copyProperties(eventEntity, this, "eventEntityDecorator");
    return eventEntity;
  }
}
