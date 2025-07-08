package com.prosilion.superconductor.entity.standard;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.ReferenceTag;
import com.prosilion.superconductor.dto.AbstractTagDto;
import com.prosilion.superconductor.dto.standard.ReferenceTagDto;
import com.prosilion.superconductor.entity.AbstractTagEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.net.URI;
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
// TODO: comprehensive unit test all parameter variants
public class ReferenceTagEntity extends AbstractTagEntity {
  private String uri;

  public ReferenceTagEntity(@NonNull ReferenceTag referenceTag) {
    super("r");
    this.uri = referenceTag.getUri().toString();
  }

  @Override
  @Transient
  public BaseTag getAsBaseTag() {
    return new ReferenceTag(URI.create(uri));
  }

  @Override
  public AbstractTagDto convertEntityToDto() {
    return new ReferenceTagDto(new ReferenceTag(URI.create(uri)));
  }

  @Override
  @Transient
  public List<String> get() {
    return List.of(uri);
  }
}
