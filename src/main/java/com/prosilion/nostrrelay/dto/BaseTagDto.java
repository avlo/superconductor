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
	private String key;

	public BaseTagDto(String id) {
		super(id);
	}

	public BaseTagEntity convertDtoToEntity() {
		BaseTagEntity baseTagEntity = new BaseTagEntity();
		try {
			BeanUtils.copyProperties(baseTagEntity, this);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return baseTagEntity;
	}
}
