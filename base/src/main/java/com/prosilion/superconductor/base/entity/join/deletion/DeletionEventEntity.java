package com.prosilion.superconductor.base.entity.join.deletion;

import com.prosilion.superconductor.base.entity.join.EventEntityAbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
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
