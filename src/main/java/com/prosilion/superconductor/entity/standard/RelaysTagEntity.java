package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.RelaysTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.base.Relay;
import nostr.event.BaseTag;
import nostr.event.tag.RelaysTag;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "relays_tag")
public class RelaysTagEntity extends AbstractTagEntity {
  private String uri;

  public RelaysTagEntity(@NonNull RelaysTag relaysTag) {
    this.uri = relaysTag.getRelays().getFirst().getUri();
  }

  @Override
  public String getCode() {
    return "relays";
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new RelaysTag(new Relay(uri));
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new RelaysTagDto(new RelaysTag(new Relay(uri)));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RelaysTagEntity that = (RelaysTagEntity) o;
    return Objects.equals(uri, that.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(uri);
  }
}
