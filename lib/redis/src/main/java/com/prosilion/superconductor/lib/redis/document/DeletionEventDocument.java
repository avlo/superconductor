package com.prosilion.superconductor.lib.redis.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@RedisHash
public class DeletionEventDocument implements DeletionEventDocumentRedisIF {

  @Id
  @NonNull
  private String eventId;

  @Override
  public String getId() {
    return eventId;
  }
}
