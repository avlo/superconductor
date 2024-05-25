package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.PriceTagDto;
import com.prosilion.superconductor.entity.join.classified.EventEntityPriceTagEntity;
import com.prosilion.superconductor.repository.classified.PriceTagEntityRepository;
import com.prosilion.superconductor.repository.join.classified.EventEntityPriceTagEntityRepository;
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
