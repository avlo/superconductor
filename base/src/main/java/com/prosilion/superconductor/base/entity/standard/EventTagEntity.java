package com.prosilion.superconductor.base.entity.standard;

import com.prosilion.nostr.enums.Marker;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.dto.standard.EventTagDto;
import com.prosilion.superconductor.base.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

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
    this(eventTag.getIdEvent(), eventTag.getMarker(), eventTag.recommendedRelayUrl());
  }

  public EventTagEntity(@NonNull String eventIdString, Marker marker, String recommendedRelayUrl) {
    super("e");
    this.eventIdString = eventIdString;
    this.marker = marker;
    this.recommendedRelayUrl = recommendedRelayUrl;
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
  @Transient
  public List<String> get() {
    return Stream.of(
            eventIdString,
            Optional.ofNullable(marker).map(Marker::getValue).toString(),
            Optional.ofNullable(recommendedRelayUrl).toString())
        .toList();
  }
}
