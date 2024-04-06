package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.PriceTagDto;
import com.prosilion.nostrrelay.entity.join.EventEntityPriceTagEntity;
import com.prosilion.nostrrelay.repository.PriceTagEntityRepository;
import com.prosilion.nostrrelay.repository.join.EventEntityPriceTagEntityRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class PriceTagEntityService {

  private final PriceTagEntityRepository priceTagEntityRepository;
  private final EventEntityPriceTagEntityRepository join;

  public PriceTagEntityService() {
    join = ApplicationContextProvider.getApplicationContext().getBean(EventEntityPriceTagEntityRepository.class);
    priceTagEntityRepository = ApplicationContextProvider.getApplicationContext().getBean(PriceTagEntityRepository.class);
  }

  public Long savePriceTag(Long entityId, PriceTagDto priceTag) throws InvocationTargetException, IllegalAccessException {
    Long savedPriceTagId = priceTagEntityRepository.save(priceTag.convertDtoToEntity()).getId();
    return join.save(new EventEntityPriceTagEntity(entityId, savedPriceTagId)).getId();
  }
}
