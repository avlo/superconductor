package com.prosilion.superconductor.lib.jpa.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.base.cache.AddressTagEventTagMappableIF;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

@Entity
@Table(name = "event")
@NoArgsConstructor
public class EventJpaEntity implements EventJpaEntityIF, AddressTagEventTagMappableIF {
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
  private List<BaseTag> tags = new ArrayList<>();

  @Transient
  private Map<AddressTag, EventTag> aTagETagMap = new HashMap<>();

  private String signature;

  public EventJpaEntity(String eventId, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this.eventId = eventId;
    this.kind = kind;
    this.pubKey = pubKey;
    this.createdAt = createdAt;
    this.signature = signature;
    this.content = content;
  }

  public EventJpaEntity(Long uid, String eventId, Integer kind, String pubKey, Long createdAt, String signature, String content) {
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
  public void setTags(List<BaseTag> baseTags) {
    this.tags = cullATagETagMapFromBaseTags(baseTags);
  }

  @Override
  public List<BaseTag> getTags() {
    return getTags(tags);
  }

  @Override
  public void setATagETagMap(@NonNull Map<AddressTag, EventTag> aTagETagMap) {
    this.aTagETagMap = aTagETagMap;
  }

  @Override
  public Map<AddressTag, EventTag> getATagETagMap() {
    return aTagETagMap;
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
    return new Signature(signature);
  }

  @Override
  public void setSignature(Signature signature) {
    this.signature = signature.toString();
  }

  @Override
  public boolean equals(Object o) {
//    boolean isInstance = (o instanceof EventIF);  useful debug coming future
    if (!(o instanceof EventIF that)) return false;

    return
       new HashSet<>(CollectionUtils.emptyIfNull(tags)).containsAll(CollectionUtils.emptyIfNull(that.getTags())) &&
          Objects.equals(eventId, that.getId()) &&
          Objects.equals(pubKey, that.getPublicKey().toString()) &&
          Objects.equals(kind, that.getKind().getValue()) &&
          Objects.equals(createdAt, that.getCreatedAt()) &&
          Objects.equals(content, that.getContent()) &&
          Objects.equals(signature, that.getSignature().toString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(uid, eventId, pubKey, kind, createdAt, content,
       new HashSet<>(CollectionUtils.emptyIfNull(tags)), // HashSet provides comparable-operative ordering 
       signature);
  }
}
