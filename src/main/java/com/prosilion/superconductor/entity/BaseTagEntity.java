package com.prosilion.superconductor.entity;

import com.prosilion.superconductor.dto.BaseTagDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Table(name = "base_tag")
public class BaseTagEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "\"key\"")
  private String key;

  @Column(name = "\"value\"")
  private String idEvent;

  private Marker marker;
  private String recommendedRelayUrl;

  public BaseTagEntity(@NonNull String idEvent, @NonNull String key, @NonNull Marker marker) {
    this.idEvent = idEvent;
    this.key = key;
    this.marker = marker;
  }

  public BaseTagDto convertEntityToDto() {
    BaseTagDto baseTagDto = new BaseTagDto(idEvent);
    BeanUtils.copyProperties(this, baseTagDto);
    return baseTagDto;
  }
}
