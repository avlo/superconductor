package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.ClassifiedListingDto;
import com.prosilion.nostrrelay.dto.PriceTagDto;
import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import com.prosilion.nostrrelay.repository.ClassifiedListingEntityRepository;
import com.prosilion.nostrrelay.service.event.join.ClassifiedListingEntityEventEntityService;
import jakarta.persistence.NoResultException;
import lombok.extern.java.Log;
import nostr.base.ElementAttribute;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.message.EventMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class ClassifiedListingEventService<T extends EventMessage> extends EventService<T> {
  private final ClassifiedListingEntityRepository classifiedListingEntityRepository;
  private final ClassifiedListingEntityEventEntityService joinService;
  private final PriceTagEntityService priceTagEntityService;

  public ClassifiedListingEventService(T eventMessage) {
    super(eventMessage);
    classifiedListingEntityRepository = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEntityRepository.class);
    joinService = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEntityEventEntityService.class);
    priceTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(PriceTagEntityService.class);
  }

  @Override
  public void processIncoming() throws InvocationTargetException, IllegalAccessException {
    log.log(Level.INFO, "processing incoming CLASSIFIED_LISTING: [{0}]", getEventMessage());
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    Long savedEventId = super.saveEventEntity(event);

    List<GenericTag> genericTagsOnly = event.getTags().stream().map(baseTag -> (GenericTag) baseTag).toList();

    ClassifiedListingDto classifiedListingDto = new ClassifiedListingDto(
        getReturnVal(genericTagsOnly, "title"),
        getReturnVal(genericTagsOnly, "summary"),
        // TODO:
        new PriceTagDto("price", "$666", "BTC", "frequency")
    );
    classifiedListingDto.setLocation(getReturnVal(genericTagsOnly, "location"));
    classifiedListingDto.setPublishedAt(event.getCreatedAt());

    ClassifiedListingEntity classifiedListingEntity = saveClassifiedListing(classifiedListingDto);

    joinService.save(savedEventId, classifiedListingEntity.getId());
    priceTagEntityService.savePriceTag(savedEventId, classifiedListingDto.getPriceTag());
  }

  private ClassifiedListingEntity saveClassifiedListing(ClassifiedListingDto classifiedListingDto) throws InvocationTargetException, IllegalAccessException {
    return Optional.of(classifiedListingEntityRepository.save(classifiedListingDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
  }

  private static String getReturnVal(List<GenericTag> genericTags, String val) {
    List<ElementAttribute> atts = genericTags.stream().filter(tag -> tag.getCode().equals(val)).findFirst().get().getAttributes();
    return (String) atts.stream().map(ea -> ea.getValue()).findFirst().get();
  }

  public ClassifiedListingEntity findById(Long id) {
    return classifiedListingEntityRepository.findById(id).get();
  }
}
