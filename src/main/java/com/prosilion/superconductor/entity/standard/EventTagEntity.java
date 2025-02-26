package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.standard.EventTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.Marker;
import nostr.event.tag.EventTag;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event_tag")
public class EventTagEntity extends AbstractTagEntity {
  private String eventIdString;
  private Marker marker;
  private String recommendedRelayUrl;

  public EventTagEntity(@NonNull EventTag eventTag) {
    this.eventIdString = eventTag.getIdEvent();
    this.marker = eventTag.getMarker();
    this.recommendedRelayUrl = eventTag.getRecommendedRelayUrl();
  }

  public EventTagEntity(@NonNull String eventIdString, Marker marker, String recommendedRelayUrl) {
    this.eventIdString = eventIdString;
    this.marker = marker;
    this.recommendedRelayUrl = recommendedRelayUrl;
  }

  @Override
  public String getCode() {
    return "e";
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new EventTag(eventIdString, recommendedRelayUrl, marker);
  }

  @Override
  public EventTagDto convertEntityToDto() {
    return new EventTagDto(new EventTag(eventIdString, recommendedRelayUrl, marker));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventTagEntity that = (EventTagEntity) o;
    return Objects.equals(eventIdString, that.eventIdString) && marker == that.marker && Objects.equals(recommendedRelayUrl, that.recommendedRelayUrl);
  }
  @Override
  public int hashCode() {
    return Objects.hash(eventIdString, marker, recommendedRelayUrl);
  }
}
