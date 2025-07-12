package prosilion.superconductor.lib.jpa.dto.standard;

import prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.IdentifierTag;
import prosilion.superconductor.lib.jpa.entity.standard.IdentifierTagEntity;

public class IdentifierTagDto implements AbstractTagDto {
  private final IdentifierTag identifierTag;

  public IdentifierTagDto(@NonNull IdentifierTag identifierTag) {
    this.identifierTag = identifierTag;
  }

  @Override
  public String getCode() {
    return identifierTag.getCode();
  }

  @Override
  public IdentifierTagEntity convertDtoToEntity() {
    return new IdentifierTagEntity(identifierTag);
  }
}
