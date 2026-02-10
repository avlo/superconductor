package com.prosilion.superconductor.lib.jpa.entity.standard;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "reference_tag")
public class ReferenceTagJpaEntity extends AbstractTagJpaEntity {
  private String uri;

  public ReferenceTagJpaEntity(@NonNull ReferenceTag referenceTag) {
    super("r");
    this.uri = referenceTag.getUrl();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new ReferenceTag(uri);
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(uri);
  }
}
