package com.prosilion.nostrrelay.dto;

import com.prosilion.nostrrelay.entity.BaseTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.tag.EventTag;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

@Setter
@Getter
public class BaseTagDto extends EventTag {
  //  private String name;

  public BaseTagDto(String id) {
    super(id);
  }

  public BaseTagEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    BaseTagEntity baseTagEntity = new BaseTagEntity();
    BeanUtils.copyProperties(baseTagEntity, this);
    //    BeanUtils.copyProperty(baseTagEntity, "tagValue", this.getIdEvent());
    //    BeanUtils.copyProperty(baseTagEntity, "name", this.getCode());
    return baseTagEntity;
  }
}
