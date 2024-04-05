package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.event.EventDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;
import nostr.base.Signature;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.event.impl.GenericTag;
import nostr.util.NostrUtil;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // List<BaseTag> to be stored in their own join table ENTITY_TAGS
  // private List<BaseTag> tags;

  private String signature;
  private String eventId;
  private String pubKey;
  private Integer kind;
  private Integer nip;
  private Long createdAt;

  @Transient
  private EventEntityDecorator eventEntityDecorator;

  public EventEntity(String eventId, Integer kind, Integer nip, String pubKey, Long createdAt, String signature, EventEntityDecorator eventEntityDecorator) {
    this.eventId = eventId;
    this.kind = kind;
    this.nip = nip;
    this.pubKey = pubKey;
    this.createdAt = createdAt;
    this.signature = signature;
    this.eventEntityDecorator = eventEntityDecorator;
  }

  public EventDto convertEntityToDto() {
    List<BaseTag> tags = List.of(
        GenericTag.create("e", 1, "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"),
        GenericTag.create("p", 1, "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")
    );

    byte[] rawData = NostrUtil.hexToBytes(signature);
    Signature signature = new Signature();
    signature.setRawData(rawData);

    EventDto eventDto = new EventDto(new PublicKey(pubKey), eventId, Kind.valueOf(kind), nip, createdAt, signature, tags, eventEntityDecorator);
    BeanUtils.copyProperties(eventDto, this);
    return eventDto;
  }
}
