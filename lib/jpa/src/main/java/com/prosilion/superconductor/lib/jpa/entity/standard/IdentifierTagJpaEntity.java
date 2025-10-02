package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.standard.IdentifierTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
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
public class IdentifierTagJpaEntity extends AbstractTagJpaEntity {
  private String uuid;

  public IdentifierTagJpaEntity(@NonNull IdentifierTag identifierTag) {
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
