package com.prosilion.superconductor.lib.redis.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@RedisHash
public class AuthNosqlEntity implements AuthNosqlEntityIF {
  @Id
  @NonNull
  @Indexed
  private String sessionId;

  @NonNull
  private String publicKey;

  @NonNull
  private String challenge;

  @NonNull
  private Long createdAt;

  @Value("${superconductor.relay.url}")
  @Transient
  private String relayUrl;
}
