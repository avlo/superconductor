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
@Table(name = "event-address_tag-join")
public class EventEntityAddressTagEntity extends EventEntityAbstractEntity {
  private Long addressTagId;

  public EventEntityAddressTagEntity(Long eventId, Long addressTagId) {
    super.setEventId(eventId);
    this.addressTagId = addressTagId;
  }
}
