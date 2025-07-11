package com.prosilion.superconductor.entity.join.subscriber;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;





import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilter implements Serializable {
  @Id
  private Long id;

  private Long subscriberId;

  private Long since;

  private Long until;

  private Integer limit;

  public SubscriberFilter(Long subscriberId, Long since, Long until, Integer limit) {
    this.subscriberId = subscriberId;
    this.since = since;
    this.until = until;
    this.limit = limit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubscriberFilter that = (SubscriberFilter) o;
    return Objects.equals(subscriberId, that.subscriberId) && Objects.equals(since, that.since) && Objects.equals(until, that.until) && Objects.equals(limit, that.limit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subscriberId, since, until, limit);
  }
}