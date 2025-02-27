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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event_tag")
public class EventTagEntity extends AbstractTagEntity {
  private String eventIdString;
  private Marker marker;
  private String recommendedRelayUrl;
  private List<String> filterField;

  public EventTagEntity(@NonNull EventTag eventTag) {
    this(eventTag.getIdEvent(), eventTag.getMarker(), eventTag.getRecommendedRelayUrl());
  }

  public EventTagEntity(@NonNull String eventIdString, Marker marker, String recommendedRelayUrl) {
    super("e");
    this.eventIdString = eventIdString;
    this.marker = marker;
    this.recommendedRelayUrl = recommendedRelayUrl;
    this.filterField = Stream.of(
            this.eventIdString,
            Optional.ofNullable(this.marker).map(Marker::getValue).toString(),
            Optional.ofNullable(this.recommendedRelayUrl).toString())
        .toList();
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
}
