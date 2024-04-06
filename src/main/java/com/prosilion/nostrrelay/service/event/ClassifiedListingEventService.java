package com.prosilion.nostrrelay.service.event;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.service.event.join.ClassifiedListingService;
import lombok.extern.java.Log;
import nostr.base.ElementAttribute;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.message.EventMessage;
import nostr.event.tag.PriceTag;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

@Log
public class ClassifiedListingEventService<T extends EventMessage> extends EventServiceImpl<T> {
  private final ClassifiedListingService classifiedListingService;

  public ClassifiedListingEventService(T eventMessage) {
    super(eventMessage);
    classifiedListingService = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingService.class);
  }

  @Override
  public void processIncoming() throws InvocationTargetException, IllegalAccessException {
    log.log(Level.INFO, "processing incoming CLASSIFIED_LISTING: [{0}]", getEventMessage());
    GenericEvent event = (GenericEvent) getEventMessage().getEvent();
    Long savedEventId = super.saveEventEntity(event);

    List<GenericTag> genericTags = event.getTags().stream().map(baseTag -> (GenericTag) baseTag).toList();

    ClassifiedListing classifiedListing = new ClassifiedListing(
        getReturnVal(genericTags, "title"),
        getReturnVal(genericTags, "summary"),
        List.of(new PriceTag("price", "$666", "BTC", "frequency"))
    );
    classifiedListing.setLocation(getReturnVal(genericTags, "location"));
    classifiedListing.setPublishedAt(event.getCreatedAt());

    classifiedListingService.save(classifiedListing, savedEventId);
  }

  private static String getReturnVal(List<GenericTag> genericTags, String val) {
    List<ElementAttribute> atts = genericTags.stream().filter(tag -> tag.getCode().equals(val)).findFirst().get().getAttributes();
    return (String) atts.stream().map(ea -> ea.getValue()).findFirst().get();
  }
}
