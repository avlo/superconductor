package prosilion.superconductor.lib.jpa.entity.standard;

import prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import prosilion.superconductor.lib.jpa.dto.standard.IdentifierTagDto;
import prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
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
