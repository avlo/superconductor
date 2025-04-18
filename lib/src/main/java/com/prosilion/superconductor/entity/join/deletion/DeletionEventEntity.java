package com.prosilion.superconductor.entity.join.deletion;

import com.prosilion.superconductor.entity.join.EventEntityAbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "deletion_event")
public class DeletionEventEntity extends EventEntityAbstractEntity {
  public DeletionEventEntity(@NonNull Long eventId) {
    super(eventId);
  }
}
