package prosilion.superconductor.lib.jpa.entity.join.classified;

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
@Table(name = "event-price_tag-join")
public class EventEntityPriceTagEntity extends EventEntityAbstractEntity {
  private Long priceTagId;

  public EventEntityPriceTagEntity(Long eventId, Long priceTagId) {
    super.setEventId(eventId);
    this.priceTagId = priceTagId;
  }
}
