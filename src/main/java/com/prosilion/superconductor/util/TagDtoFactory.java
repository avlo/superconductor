package com.prosilion.superconductor.util;

import com.prosilion.superconductor.dto.GenericTagDto;
import com.prosilion.superconductor.dto.GeohashTagDto;
import com.prosilion.superconductor.dto.HashtagTagDto;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGeohashTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityHashtagTagEntity;
import org.springframework.stereotype.Component;

@Component
public class TagDtoFactory {
  public static GenericTagDto createDto(String code, String value) {
    switch (code) {
//      case "d":
//        // identity
      case "g":
        return new GeohashTagDto(value);
//      case "r":
//        // code block
      case "t":
        return new HashtagTagDto(value);
//      case "u":
//        // code block
      default:
        return new GeohashTagDto("NULL");
    }
  }

  public static EventEntityGenericTagEntity createEntity(String code, Long eventId, Long tagId) {
    switch (code) {
//      case "d":
//        // identity
      case "g":
        return new EventEntityGeohashTagEntity(eventId, tagId);
//      case "r":
//        // code block
      case "t":
        return new EventEntityHashtagTagEntity(eventId, tagId);
//      case "u":
//        // code block
      default:
        return new EventEntityGeohashTagEntity(eventId, tagId);
    }
  }
}
