package com.prosilion.superconductor.base.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class Subscriber implements Serializable {
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
