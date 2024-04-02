package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.BaseTagDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

  @Column(name = "\"value\"")
  private String idEvent;
  //  private String name;
  //  private String tagValue;
  private String recommendedRelayUrl;
  private Marker marker;

  public BaseTagDto convertEntityToDto() {
    BaseTagDto baseTagDto = new BaseTagDto(idEvent);
    BeanUtils.copyProperties(baseTagDto, this);
    return baseTagDto;
  }
}
