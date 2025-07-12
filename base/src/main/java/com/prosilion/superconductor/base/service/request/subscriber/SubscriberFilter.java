package com.prosilion.superconductor.base.service.request.subscriber;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@NoArgsConstructor
@Component
public class SubscriberFilter implements Serializable {
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
