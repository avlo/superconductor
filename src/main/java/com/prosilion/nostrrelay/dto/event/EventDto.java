package com.prosilion.nostrrelay.dto.event;

import com.prosilion.nostrrelay.entity.EventEntity;
import nostr.base.PublicKey;
import nostr.base.Signature;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.NIP01Event;
import org.springframework.beans.BeanUtils;

import java.util.List;
public class EventDto extends NIP01Event {

  public EventDto(PublicKey pubKey, String eventId, Kind kind, Integer nip, Long createdAt, Signature signature, List<BaseTag> tags, String content) {
    super(pubKey, kind, tags);
    setId(eventId);
    setNip(nip);
    setCreatedAt(createdAt);
    setSignature(signature);
    setContent(content);
  }

  public EventEntity convertDtoToEntity() {
    EventEntity eventEntityImpl = new EventEntity(getId(), getKind(), getNip(), getPubKey().toString(), getCreatedAt(), getSignature().toString(), getContent());
    BeanUtils.copyProperties(eventEntityImpl, this, "tags");
    return eventEntityImpl;
  }
}
