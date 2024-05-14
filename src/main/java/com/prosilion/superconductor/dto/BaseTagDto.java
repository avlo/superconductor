package com.prosilion.superconductor.dto;

import com.prosilion.superconductor.entity.BaseTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.tag.EventTag;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
public class BaseTagDto extends EventTag {
  private String key;

  public BaseTagDto(String id) {
    super(id);
  }

  public BaseTagEntity convertDtoToEntity() {
    BaseTagEntity baseTagEntity = new BaseTagEntity();
    BeanUtils.copyProperties(this, baseTagEntity);
    return baseTagEntity;
  }
}
