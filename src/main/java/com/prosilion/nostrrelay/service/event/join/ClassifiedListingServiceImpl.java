package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import com.prosilion.nostrrelay.entity.join.ClassifiedListingEventTagEntity;
import com.prosilion.nostrrelay.repository.ClassifiedListingRepository;
import com.prosilion.nostrrelay.repository.join.ClassifiedListingEventTagEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import nostr.event.impl.ClassifiedListingEvent;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.tag.PriceTag;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@Service
public class ClassifiedListingServiceImpl {
  private final ClassifiedListingRepository classifiedListingRepository;
  private final ClassifiedListingEventTagEntityRepository classifiedListingEventTagEntityRepository;

  public ClassifiedListingServiceImpl() {
    classifiedListingRepository = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingRepository.class);
    classifiedListingEventTagEntityRepository = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEventTagEntityRepository.class);
  }

  @Transactional
  public Long save(ClassifiedListingEvent event, Long eventId) throws InvocationTargetException, IllegalAccessException {
    PriceTag priceTag = new PriceTag("price", "$666", "BTC", "frequency");
    ClassifiedListing classifiedListing = new ClassifiedListing(
        event.getClassifiedListing().getTitle(),
        event.getClassifiedListing().getSummary(), List.of(priceTag));
    classifiedListing.setLocation(event.getClassifiedListing().getLocation());
    classifiedListing.setPublishedAt(event.getClassifiedListing().getPublishedAt());
    Long classifiedListingId = saveClassifiedListing(classifiedListing).getId();
    return saveClassifiedListingJoin(eventId, classifiedListingId).getId();
  }

  private ClassifiedListingEntity saveClassifiedListing(ClassifiedListing classifiedListing) {
    return Optional.of(classifiedListingRepository.save(
            new ClassifiedListingEntity(
                classifiedListing.getTitle(),
                classifiedListing.getSummary(),
                classifiedListing.getLocation(),
                classifiedListing.getPublishedAt())))
        .orElseThrow(NoResultException::new);
  }

  private ClassifiedListingEventTagEntity saveClassifiedListingJoin(Long eventId, Long classifiedListingId) {
    return Optional.of(classifiedListingEventTagEntityRepository.save(new ClassifiedListingEventTagEntity(eventId, classifiedListingId))).orElseThrow(NoResultException::new);
  }

  public ClassifiedListingEntity findById(Long id) {
    return classifiedListingRepository.findById(id).get();
  }
}
