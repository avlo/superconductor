package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.EventEntity;
import nostr.base.Kind;
import nostr.base.PublicKey;
import nostr.base.Signature;
import nostr.event.BaseTag;
import nostr.event.NIP01Event;

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
    return new EventEntity(getId(), getKind(), getNip(), getPubKey().toString(), getCreatedAt(), getSignature().toString(), getContent());
  }
}
