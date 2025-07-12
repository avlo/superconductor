package prosilion.superconductor.lib.jpa.entity.join.deletion;

import com.prosilion.superconductor.base.DeletionEventEntityIF;
import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
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
public class DeletionEventEntity extends EventEntityAbstractEntity implements DeletionEventEntityIF {
  public DeletionEventEntity(@NonNull Long eventId) {
    super(eventId);
  }

  @Override
  public Long getEventId() {
    return super.getEventId();
  }
}
