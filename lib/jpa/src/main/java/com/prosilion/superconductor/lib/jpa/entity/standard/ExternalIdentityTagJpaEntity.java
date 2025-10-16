package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import com.prosilion.superconductor.lib.jpa.dto.standard.ExternalIdentityTagDto;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "external_identity_tag")
public class ExternalIdentityTagJpaEntity extends AbstractTagJpaEntity {
  private Integer kind;
  private String uuid;
  private String formula;

  public ExternalIdentityTagJpaEntity(@NonNull ExternalIdentityTag externalIdentityTag) {
    super("i");
    this.kind = externalIdentityTag.getKind().getValue();
    this.uuid = externalIdentityTag.getIdentifierTag().getUuid();
    this.formula = externalIdentityTag.getFormula();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return createExternalIdentityTag();
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new ExternalIdentityTagDto(createExternalIdentityTag());
  }

  @Override
  @Transient
  public List<String> get() {
    return Stream.of(
            kind.toString(),
            uuid,
            formula)
        .toList();
  }

  //  TODO: stream-ify below
  private ExternalIdentityTag createExternalIdentityTag() {
    return new ExternalIdentityTag(Kind.valueOf(kind), new IdentifierTag(uuid), formula);
  }
}
