package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.EventIF;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@RedisHash
public class EventDocument implements EventIF {

  @Id
  @NonNull
  private String eventIdString;

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
  private List<BaseTag> tags;

  @NonNull
  private String signature;
}
