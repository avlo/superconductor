package com.prosilion.superconductor.entity.join;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;





import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
public abstract class EventEntityAbstractEntity implements Serializable {
  @Id
  private Long id;
  private Long eventId;

  public EventEntityAbstractEntity(Long eventId) {
    this.eventId = eventId;
  }
}
