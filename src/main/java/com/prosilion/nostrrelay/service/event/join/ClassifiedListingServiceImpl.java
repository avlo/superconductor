package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import com.prosilion.nostrrelay.entity.join.ClassifiedListingEntityEventEntityJoin;
import com.prosilion.nostrrelay.repository.ClassifiedListingRepository;
import com.prosilion.nostrrelay.repository.join.ClassifiedListingEntityEventEntityRepositoryJoin;
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
  private final ClassifiedListingEntityEventEntityRepositoryJoin joinTable;

  public ClassifiedListingServiceImpl() {
    classifiedListingRepository = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingRepository.class);
    joinTable = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEntityEventEntityRepositoryJoin.class);
  }

  @Transactional
  public Long save(ClassifiedListing classifiedListing, Long eventId) throws InvocationTargetException, IllegalAccessException {
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

  private ClassifiedListingEntityEventEntityJoin saveClassifiedListingJoin(Long eventId, Long classifiedListingId) {
    return Optional.of(joinTable.save(new ClassifiedListingEntityEventEntityJoin(eventId, classifiedListingId))).orElseThrow(NoResultException::new);
  }

  public ClassifiedListingEntity findById(Long id) {
    return classifiedListingRepository.findById(id).get();
  }
}
