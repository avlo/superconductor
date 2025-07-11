package com.prosilion.superconductor.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.lang.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("subscriber")
public class Subscriber implements Serializable {
  @Id
  private Long subscriberSessionHash;

  private String subscriberId;
  private String sessionId;
  private boolean active;

  public Subscriber(@NonNull String subscriberId, @NonNull String sessionId, boolean active) {
    this.subscriberId = subscriberId;
    this.sessionId = sessionId;
    this.active = active;
  }
}
