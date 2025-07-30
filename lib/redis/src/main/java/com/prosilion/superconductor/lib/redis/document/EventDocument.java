package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@RedisHash
public class EventDocument implements EventDocumentIF {

  @Id
  @NonNull
  private String eventId;

  @NonNull
  @Indexed
  private Integer kind;

  @NonNull
  @Indexed
  private String pubKey;

  @NonNull
  private Long createdAt;

  @NonNull
  private String content;

  @Indexed
  @Setter
  private List<BaseTag> tags = new ArrayList<>();

  @NonNull
  private String signature;

  @Override
  public String getId() {
    return eventId;
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
}
