package com.prosilion.superconductor.lib.redis.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@RedisHash
public class EventNosqlEntity implements EventNosqlEntityIF {

  @Id
  @NonNull
  private String eventId;

  @NonNull
  @Indexed
  private Integer kind;

  @NonNull
  @Indexed
  private String publicKey;

  @NonNull
  private Long createdAt;

  @NonNull
  private String content;

  @Indexed
  private List<BaseTag> tags = new ArrayList<>();

  @Indexed
  private Map<AddressTag, EventTag> aTagETagMap = new HashMap<>();

  @NonNull
  private String signature;

  @Override
  public String getId() {
    return eventId;
  }

  @Override
  public PublicKey getPublicKey() {
    return new PublicKey(publicKey);
  }

  @Override
  public Kind getKind() {
    return Kind.valueOf(kind);
  }

  @Override
  public List<BaseTag> getTags() {
    return getTags(tags);
  }

  public void setTags(List<BaseTag> tags) {
    this.tags = cullATagETagMapFromBaseTags(tags);
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
  public Signature getSignature() {
    return new Signature(signature);
  }
}
