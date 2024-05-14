package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.EventDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;
import nostr.base.Signature;
import nostr.event.BaseTag;
import nostr.event.Kind;
import nostr.util.NostrUtil;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String signature;
  private String eventId;
  private String pubKey;
  private Integer kind;
  private Integer nip;
  private Long createdAt;

  @Lob
  private String content;

  @Transient
  private List<BaseTag> tags;

  public EventEntity(String eventId, Integer kind, Integer nip, String pubKey, Long createdAt, String signature, String content) {
    this.eventId = eventId;
    this.kind = kind;
    this.nip = nip;
    this.pubKey = pubKey;
    this.createdAt = createdAt;
    this.signature = signature;
    this.content = content;
  }

  public EventDto convertEntityToDto() {
    byte[] rawData = NostrUtil.hexToBytes(signature);
    Signature signature = new Signature();
    signature.setRawData(rawData);
    return new EventDto(new PublicKey(pubKey), eventId, Kind.valueOf(kind), nip, createdAt, signature, tags, content);
  }
}
