package com.prosilion.superconductor.entity.standard;

import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.event.BaseTag;
import nostr.event.tag.IdentifierTag;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "identifier_tag")
public class IdentifierTagEntity extends AbstractTagEntity {
  private String uuid;

  public IdentifierTagEntity(@NonNull IdentifierTag identifierTag) {
    super("d");
    this.uuid = identifierTag.getUuid();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new IdentifierTag(uuid);
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new IdentifierTagDto(new IdentifierTag(uuid));
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(uuid);
  }
}
