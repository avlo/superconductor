package com.prosilion.superconductor.entity.join.deletion;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;
import org.springframework.data.redis.core.RedisHash;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@RedisHash("deletion_event")
public class DeletionEventEntity extends EventEntityAbstractEntity {
  public DeletionEventEntity(@NonNull Long eventId) {
    super(eventId);
  }
}
