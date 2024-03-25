package com.prosilion.nostrrelay.service;

import lombok.extern.java.Log;
import nostr.api.NIP99;
import nostr.base.IEvent;
import nostr.event.BaseTag;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import nostr.event.tag.PriceTag;
import nostr.id.Identity;

import java.util.List;
import java.util.logging.Level;

@Log
public class ClassifiedEventServiceImpl<T extends EventMessage> extends EventServiceImpl<T> implements ClassifiedEventService {
  private final ClassifiedListing classifiedListing;

  public ClassifiedEventServiceImpl(T eventMessage) {
    super(eventMessage);
    this.classifiedListing = new ClassifiedListing();
    GenericEvent event = (GenericEvent) eventMessage.getEvent();
    List<BaseTag> tags = event.getTags();
    this.classifiedListing.setTitle(getTagValue(tags, 0));
    this.classifiedListing.setSummary(getTagValue(tags, 1));
    this.classifiedListing.setLocation(getTagValue(tags, 2));
    this.classifiedListing.setCurrency(getTagValue(tags, 4));
  }

  @Override
  public IEvent processIncoming() {
    log.log(Level.INFO, "processing incoming CLASSIFIED_LISTING: [{0}]", getEventMessage());
    PriceTag priceTag = new PriceTag("price", "$666", "BTC", "frequency");
    return new NIP99<>(Identity.getInstance()).createClassifiedListingEvent(classifiedListing, priceTag).getEvent();
  }

}
