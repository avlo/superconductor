package com.prosilion.superconductor.util;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import com.prosilion.superconductor.dto.HashtagTagDto;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityHashtagTagEntity;
import org.springframework.stereotype.Component;

@Component
public class TagDtoFactory<T extends EventEntityGenericTagEntity> {
  public GenericTagDto createDto(Character code, String value) {
    return switch (code) {
//      case "d":
//        // identity
      case 'g' -> new GeohashTagDto(value);
//      case "r":
//        // code block
      case 't' -> new HashtagTagDto(value);
//      case "u":
//        // code block
      default -> new GeohashTagDto("NULL");
    };
  }

  public T createEntity(Character code, Long eventId, Long tagId) {
    return switch (code) {
//      case "d":
//        // identity
      case 'g' -> {
        EventEntityGeohashTagEntity eventEntityGeohashTagEntity = new EventEntityGeohashTagEntity(eventId, tagId);
        yield (T) eventEntityGeohashTagEntity;
      }
//      case "r":
//        // code block
      case 't' -> {
        EventEntityHashtagTagEntity eventEntityHashtagTagEntity = new EventEntityHashtagTagEntity(eventId, tagId);
        yield (T) eventEntityHashtagTagEntity;
      }
//      case "u":
//        // code block
      default -> {
        EventEntityGeohashTagEntity eventEntityGeohashTagEntity = new EventEntityGeohashTagEntity(eventId, tagId);
        yield (T) eventEntityGeohashTagEntity;
      }
    };
  }
}
