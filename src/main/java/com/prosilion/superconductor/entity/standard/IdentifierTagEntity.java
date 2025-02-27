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

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "identifier_tag")
public class IdentifierTagEntity extends AbstractTagEntity {
  private String identifier;
  private List<String> filterField;

  public IdentifierTagEntity(@NonNull IdentifierTag identifierTag) {
    super("d");
    this.identifier = identifierTag.getId();
    this.filterField = List.of(this.identifier);
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
}
