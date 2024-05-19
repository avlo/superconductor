package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.classified.RelaysTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.base.ElementAttribute;
import nostr.event.tag.RelaysTag;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Setter
@Getter
public class RelaysTagDto extends RelaysTag {

  public RelaysTagDto(List<String> relayUris) {
    super(relayUris);
  }

  public RelaysTagEntity convertDtoToEntity() {
    RelaysTagEntity relaysTagEntity = new RelaysTagEntity();
    BeanUtils.copyProperties(this, relaysTagEntity);
    return relaysTagEntity;
  }

  public static RelaysTagDto createRelaysTagDtoFromAttributes(List<ElementAttribute> atts) {
    return new RelaysTagDto(atts.stream().skip(1).map(ElementAttribute::toString).toList());
  }
}
