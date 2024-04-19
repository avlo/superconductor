package com.prosilion.nostrrelay.dto;

import com.prosilion.nostrrelay.entity.PriceTagEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.base.ElementAttribute;
import nostr.event.tag.PriceTag;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Setter
@Getter
public class PriceTagDto extends PriceTag {

	public PriceTagDto(String number, String currency, String frequency) {
		super(number, currency, frequency);
	}

	public PriceTagEntity convertDtoToEntity() {
		PriceTagEntity priceTagEntity = new PriceTagEntity();
		try {
			BeanUtils.copyProperties(priceTagEntity, this);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return priceTagEntity;
	}

	public static PriceTagDto createPriceTagDtoFromAttributes(List<ElementAttribute> atts) {
//    TODO: refactor
		String one = atts.get(0).getValue().toString();
		String two = atts.get(1).getValue().toString();
		String three = atts.get(2).getValue().toString();
		return new PriceTagDto(one, two, three);
	}
}
