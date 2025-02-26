package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.IdentifierTag;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "identifier_tag")
public class IdentifierTagEntity extends AbstractTagEntity {
  private String identifier;

  public IdentifierTagEntity(@NonNull IdentifierTag identifierTag) {
    this.identifier = identifierTag.getId();
  }

  @Override
  public String getCode() {
    return "d";
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new IdentifierTag(identifier);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new IdentifierTagDto(new IdentifierTag(identifier));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdentifierTagEntity that = (IdentifierTagEntity) o;
    return Objects.equals(identifier, that.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(identifier);
  }
}
