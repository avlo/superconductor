package com.prosilion.superconductor.base.entity.standard;

import com.prosilion.superconductor.base.dto.AbstractTagDto;
import com.prosilion.superconductor.base.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.base.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import lombok.Setter;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;

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
