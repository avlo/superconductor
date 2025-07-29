package com.prosilion.superconductor.lib.jpa.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity implements EventEntityIF {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String eventIdString;
  private String pubKey;
  private Integer kind;
  private Long createdAt;

  @Lob
  private String content;

  @Transient
  @Setter
  private List<BaseTag> tags;
  private String signature;

  public EventEntity(String eventIdString, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this.eventIdString = eventIdString;
    this.kind = kind;
    this.pubKey = pubKey;
    this.createdAt = createdAt;
    this.signature = signature;
    this.content = content;
  }

  public EventEntity(Long id, String eventIdString, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this(eventIdString, kind, pubKey, createdAt, signature, content);
    this.id = id;
  }

  @Override
  public String getEventId() {
    return eventIdString;
  }

  @Override
  public PublicKey getPublicKey() {
    return new PublicKey(pubKey);
  }
  
  @Override
  public Kind getKind() {
    return Kind.valueOf(kind);
  }

  @Override
  public Signature getSignature() {
    return Signature.fromString(signature);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    EventEntity that = (EventEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(eventIdString, that.eventIdString) && Objects.equals(pubKey, that.pubKey) && Objects.equals(kind, that.kind) && Objects.equals(createdAt, that.createdAt) && Objects.equals(content, that.content) && Objects.equals(tags, that.tags) && Objects.equals(signature, that.signature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, eventIdString, pubKey, kind, createdAt, content, tags, signature);
  }
}
