package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.EventTagDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.Marker;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "event_standard_tag")
public class EventStandardTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "\"key\"")
  private String key;

  @Column(name = "\"value\"")
  private String idEvent;

  private Marker marker;
  private String recommendedRelayUrl;

  public EventStandardTagEntity(@NonNull String idEvent, @NonNull String key, @NonNull Marker marker) {
    this.idEvent = idEvent;
    this.key = key;
    this.marker = marker;
  }

  public EventTagDto convertEntityToDto() {
    EventTagDto eventTagDto = new EventTagDto(idEvent);
    BeanUtils.copyProperties(this, eventTagDto);
    return eventTagDto;
  }
}
