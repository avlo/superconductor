package com.prosilion.superconductor.dto.generic;

import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityHashtagTagEntity;
import org.springframework.stereotype.Component;

@Component
public class GenericTagDtoFactory<T extends EventEntityGenericTagEntity> {
  public GenericTagDto createDto(Character code, String value) {
    return switch (code) {
      case 'g' -> new GeohashTagDto(value);
      case 't' -> new HashtagTagDto(value);
//      case "d":
//        // identity
//      case "r":
//        // code block
//      case "u":
//        // code block
      default -> throw new IllegalArgumentException(String.format("Cannot create GenericTagDto, unrecognized code [%s]", code));
    };
  }

  public T createEntity(Character code, Long eventId, Long tagId) {
    return switch (code) {
      case 'g' -> (T) new EventEntityGeohashTagEntity(eventId, tagId);
      case 't' -> (T) new EventEntityHashtagTagEntity(eventId, tagId);
//      case "d":
//        // identity
//      case "r":
//        // code block
//      case "u":
//        // code block
      default -> throw new IllegalArgumentException(String.format("Cannot create GenericTagEntity, unrecognized code [%s]", code));
    };
  }
}
