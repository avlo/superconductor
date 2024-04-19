package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.dto.PriceTagDto;
import com.prosilion.nostrrelay.entity.join.EventEntityPriceTagEntity;
import com.prosilion.nostrrelay.repository.PriceTagEntityRepository;
import com.prosilion.nostrrelay.repository.join.EventEntityPriceTagEntityRepository;
import org.springframework.stereotype.Service;

@Service
public class PriceTagEntityService {

	private final PriceTagEntityRepository priceTagEntityRepository;
	private final EventEntityPriceTagEntityRepository join;

	public PriceTagEntityService(PriceTagEntityRepository priceTagEntityRepository, EventEntityPriceTagEntityRepository join) {
		this.priceTagEntityRepository = priceTagEntityRepository;
		this.join = join;
	}

	public Long savePriceTag(Long entityId, PriceTagDto priceTag) {
		Long savedPriceTagId = priceTagEntityRepository.save(priceTag.convertDtoToEntity()).getId();
		return join.save(new EventEntityPriceTagEntity(entityId, savedPriceTagId)).getId();
	}
}
