package com.prosilion.superconductor.lib.jpa.entity.join.deletion;

import com.prosilion.superconductor.lib.jpa.dto.deletion.DeletionEventEntityJpaIF;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "deletion_event")
public class DeletionEventEntity extends EventEntityAbstractEntity implements DeletionEventEntityJpaIF {
  public DeletionEventEntity(@NonNull Long eventId) {
    super(eventId);
  }

  @Override
  public Long getId() {
    return super.getEventId();
  }
}
