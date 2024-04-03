package com.prosilion.nostrrelay.service.event.classified;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.service.event.EventServiceImpl;
import com.prosilion.nostrrelay.service.event.join.EventTagEntityServiceImpl;
import lombok.extern.java.Log;
import nostr.api.NIP99;
import nostr.base.IEvent;
import nostr.event.BaseTag;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.event.tag.PriceTag;
import nostr.id.Identity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

@Log
public class ClassifiedEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> implements ClassifiedEventService<T> {
  private final EventTagEntityServiceImpl eventTagEntityService;

  public ClassifiedEventServiceImpl(T eventMessage) {
    super(eventMessage);
    eventTagEntityService = ApplicationContextProvider.getApplicationContext().getBean(EventTagEntityServiceImpl.class);
  }

  @Override
  public IEvent processIncoming() throws InvocationTargetException, IllegalAccessException {
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    log.log(Level.INFO, "processing incoming CLASSIFIED_LISTING: [{0}]", getEventMessage());

    ClassifiedListing classifiedListing = new ClassifiedListing();
    List<BaseTag> tags = event.getTags();
//    tags.get(0).getNip()
    classifiedListing.setTitle(getTagValue(tags, 0));
    classifiedListing.setSummary(getTagValue(tags, 1));
    classifiedListing.setLocation(getTagValue(tags, 2));

    PriceTag priceTag = new PriceTag("price", "$666", "BTC", "frequency");
    NIP99<ClassifiedListingEvent> nip99Event = new NIP99<>(Identity.getInstance(event.getPubKey().toString()));
    NIP99<ClassifiedListingEvent> classifiedListingEvent = nip99Event.createClassifiedListingEvent(classifiedListing, priceTag);

    eventTagEntityService.save(classifiedListingEvent.getEvent(), getEventMessage().getEvent().getId());
    return new NIP99<>(Identity.getInstance()).createClassifiedListingEvent(classifiedListing, priceTag).getEvent();
  }

}
