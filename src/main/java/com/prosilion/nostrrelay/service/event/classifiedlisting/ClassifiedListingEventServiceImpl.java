package com.prosilion.nostrrelay.service.event.classifiedlisting;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.dto.event.ClassifiedListingEventDto;
import com.prosilion.nostrrelay.entity.ClassifiedListingEventEntity;
import com.prosilion.nostrrelay.repository.join.ClassifiedListingEventRepositoryJoin;
import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import com.prosilion.nostrrelay.service.event.join.ClassifiedListingServiceImpl;
import com.prosilion.nostrrelay.service.event.join.EventTagEntityServiceImpl;
import jakarta.persistence.NoResultException;
import lombok.extern.java.Log;
import nostr.api.NIP99;
import nostr.base.ElementAttribute;
import nostr.base.IEvent;
import nostr.event.BaseTag;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.message.EventMessage;
import nostr.event.tag.PriceTag;
import nostr.id.Identity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Log
public class ClassifiedListingEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> {
  private final ClassifiedListingEventRepositoryJoin classifiedListingEventRepository;
  private final ClassifiedListingServiceImpl classifiedListingService;
  private final EventTagEntityServiceImpl eventTagEntityService;

  public ClassifiedListingEventServiceImpl(T eventMessage) {
    super(eventMessage);
    classifiedListingEventRepository = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEventRepositoryJoin.class);
    classifiedListingService = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingServiceImpl.class);
    eventTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityServiceImpl.class);
  }

  @Override
  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    log.log(Level.INFO, "processing incoming CLASSIFIED_LISTING: [{0}]", getEventMessage());

    PriceTag priceTag = new PriceTag("price", "$666", "BTC", "frequency");

    List<BaseTag> baseTags = event.getTags().stream()
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();

    event.getTags().removeAll(baseTags);
    List<GenericTag> genericTags = event.getTags().stream().map(baseTag -> (GenericTag) baseTag).toList();

    ClassifiedListing classifiedListing = new ClassifiedListing(
        getReturnVal(genericTags, "title"),
        getReturnVal(genericTags, "summary"),
        List.of(priceTag)
    );
    classifiedListing.setLocation(getReturnVal(genericTags, "location"));
    classifiedListing.setPublishedAt(event.getCreatedAt());
    NIP99<ClassifiedListingEvent> nip99Event = new NIP99<>(Identity.getInstance(event.getPubKey().toString()));
    NIP99<ClassifiedListingEvent> classifiedListingEvent = nip99Event.createClassifiedListingEvent(baseTags, event.getContent(), classifiedListing);

    Long savedEventId = saveEntity(classifiedListingEvent.getEvent(), getEventMessage().getEvent().getId(), classifiedListing);
    eventTagEntityService.save(classifiedListingEvent.getEvent(), savedEventId);

    classifiedListingService.save(classifiedListingEvent.getEvent(), savedEventId);
    return new NIP99<>(Identity.getInstance()).createClassifiedListingEvent(baseTags, event.getContent(), classifiedListing).getEvent();
  }

  private static String getReturnVal(List<GenericTag> genericTags, String val) {
    List<ElementAttribute> atts = genericTags.stream().filter(tag -> tag.getCode().equals(val)).findFirst().get().getAttributes();
    String eaa = (String)atts.stream().map(ea -> ea.getValue()).findFirst().get();
    return eaa;
  }

  private Long saveEntity(ClassifiedListingEvent event, String id, ClassifiedListing classifiedListing) throws InvocationTargetException, IllegalAccessException, NoResultException {
    ClassifiedListingEventDto classifiedListingEventDto = new ClassifiedListingEventDto(
        event.getPubKey(),
        event.getTags(),
        event.getContent(),
        classifiedListing
    );
    classifiedListingEventDto.setId(id);
    classifiedListingEventDto.setCreatedAt(event.getCreatedAt());
    classifiedListingEventDto.setSignature(event.getSignature());
    ClassifiedListingEventEntity classifiedListingEventEntity = Optional.of(classifiedListingEventRepository.save(classifiedListingEventDto.convertDtoToEntity())).orElseThrow(NoResultException::new);
    return classifiedListingEventEntity.getId();
  }
}
