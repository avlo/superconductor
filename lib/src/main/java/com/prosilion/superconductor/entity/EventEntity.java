package com.prosilion.superconductor.entity;

import com.prosilion.nostr.crypto.NostrUtil;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("event")
public class EventEntity {
  @Id
  private long id;

  @Indexed
  private String eventIdString;
  @Indexed
  private String pubKey;
  private Integer kind;
  private Long createdAt;

  private String content;

  @Transient
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

  public EventEntity(long id, String eventIdString, Integer kind, String pubKey, Long createdAt, String signature, String content) {
    this(eventIdString, kind, pubKey, createdAt, signature, content);
    this.id = id;
  }

  public <T extends GenericEventKindIF> T convertEntityToDto() {
    byte[] rawData = NostrUtil.hex128ToBytes(signature);
    final Signature signature = new Signature();
    signature.setRawData(rawData);
    return (T) new GenericEventKind(
        eventIdString,
        new PublicKey(pubKey),
        createdAt,
        Kind.valueOf(kind),
        tags,
        content,
        signature);
  }
}
