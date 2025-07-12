package prosilion.superconductor.lib.jpa.dto.standard;

import prosilion.superconductor.lib.jpa.dto.AbstractTagDto;
import org.springframework.lang.NonNull;
import com.prosilion.nostr.tag.PubKeyTag;
import prosilion.superconductor.lib.jpa.entity.standard.PubkeyTagEntity;

public class PubkeyTagDto implements AbstractTagDto {
  private final PubKeyTag pubKeyTag;

  public PubkeyTagDto(@NonNull PubKeyTag pubKeyTag) {
    this.pubKeyTag = pubKeyTag;
  }

  @Override
  public String getCode() {
    return pubKeyTag.getCode();
  }

  @Override
  public PubkeyTagEntity convertDtoToEntity() {
    return new PubkeyTagEntity(pubKeyTag);
  }
}
