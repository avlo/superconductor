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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event")
@NoArgsConstructor
public class EventEntity implements EventEntityIF {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uid;

  @Column(unique = true)
  private String eventId;
  private String pubKey;

  private Integer kind;
  private Long createdAt;

  @Lob
  private String content;

  @Transient
  private List<BaseTag> tags;
  private String signature;

  public EventEntity(String eventId, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this.eventId = eventId;
    this.kind = kind;
    this.pubKey = pubKey;
    this.createdAt = createdAt;
    this.signature = signature;
    this.content = content;
  }

  public EventEntity(Long uid, String eventId, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this(eventId, kind, pubKey, createdAt, signature, content);
    this.uid = uid;
  }

  @Override
  public void setUid(Long uid) {
    this.uid = uid;
  }

  @Override
  public Long getUid() {
    return uid;
  }

  @Override
  public String getId() {
    return eventId;
  }

  @Override
  public void setId(String id) {
    this.eventId = id;
  }

  @Override
  public PublicKey getPublicKey() {
    return new PublicKey(pubKey);
  }

  @Override
  public void setPublicKey(PublicKey publicKey) {
    this.pubKey = publicKey.toString();
  }

  @Override
  public Kind getKind() {
    return Kind.valueOf(kind);
  }

  @Override
  public void setKind(Kind kind) {
    this.kind = kind.getValue();
  }

  @Override
  public void setTags(List<BaseTag> tags) {
    this.tags = tags;
  }

  @Override
  public List<BaseTag> getTags() {
    return tags;
  }

  @Override
  public Long getCreatedAt() {
    return createdAt;
  }

  @Override
  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public Signature getSignature() {
    return Signature.fromString(signature);
  }

  @Override
  public void setSignature(Signature signature) {
    this.signature = signature.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    EventEntity that = (EventEntity) o;
    return
        new HashSet<>(tags).containsAll(that.tags) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(pubKey, that.pubKey) &&
            Objects.equals(kind, that.kind) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(content, that.content) &&
            Objects.equals(signature, that.signature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uid, eventId, pubKey, kind, createdAt, content, new HashSet<>(tags), signature);
  }
}
