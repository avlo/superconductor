package prosilion.superconductor.lib.jpa.entity.join.standard;

import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event-reference_tag-join")
public class EventEntityReferenceTagEntity extends EventEntityAbstractEntity {
  private Long referenceTagId;

  public EventEntityReferenceTagEntity(Long eventId, Long referenceTagId) {
    super.setEventId(eventId);
    this.referenceTagId = referenceTagId;
  }
}
