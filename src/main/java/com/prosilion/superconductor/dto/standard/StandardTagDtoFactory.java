package com.prosilion.superconductor.dto.standard;

import com.prosilion.superconductor.entity.join.standard.EventEntityEventTagEntity;
import com.prosilion.superconductor.entity.join.standard.EventEntityPubkeyTagEntity;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import nostr.event.BaseTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.PubKeyTag;
import org.springframework.stereotype.Component;

@Component
public class StandardTagDtoFactory<T extends EventEntityStandardTagEntity> {
  public StandardTagDto createDto(BaseTag baseTag) {
    char code = baseTag.getCode().charAt(0);
    return switch (code) {
      case 'e' -> new EventTagDto((EventTag) baseTag);
      case 'p' -> new PubkeyTagDto((PubKeyTag) baseTag);
//      case "a":
//        // code block
      default -> throw new IllegalArgumentException(String.format("Cannot create StandardTagDto, unrecognized code [%s]", code));
    };
  }

  public T createEntity(StandardTagEntity entity, Long id) {
    Character code = entity.getCode();
    return switch (code) {
      case 'e' -> (T) new EventEntityEventTagEntity(entity.getId(), id);
      case 'p' -> (T) new EventEntityPubkeyTagEntity(entity.getId(), id);
//      case "a":
//        // code block
      default -> throw new IllegalArgumentException(String.format("Cannot create StandardTagEntity, unrecognized code [%s]", code));
    };
  }
}
