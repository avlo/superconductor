package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
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
  private String platform;
  private String identity;
  private String proof;

  public ExternalIdentityTagJpaEntity(@NonNull ExternalIdentityTag externalIdentityTag) {
    super("i");
    this.platform = externalIdentityTag.getPlatform();
    this.identity = externalIdentityTag.getIdentity();
    this.proof = externalIdentityTag.getProof();
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
    return Stream.of(platform, identity, proof).toList();
  }

  //  TODO: stream-ify below
  private ExternalIdentityTag createExternalIdentityTag() {
    return new ExternalIdentityTag(platform, identity, proof);
  }
}
