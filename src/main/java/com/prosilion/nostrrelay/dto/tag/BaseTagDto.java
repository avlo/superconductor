package com.prosilion.nostrrelay.dto.tag;

import com.prosilion.nostrrelay.entity.BaseTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.event.tag.EventTag;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

@Setter
@Getter
public class BaseTagDto extends EventTag {
  private String key;

  public BaseTagDto(String id) {
    super(id);
  }

  public BaseTagEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    BaseTagEntity baseTagEntity = new BaseTagEntity();
    BeanUtils.copyProperties(baseTagEntity, this);
    return baseTagEntity;
  }
}
