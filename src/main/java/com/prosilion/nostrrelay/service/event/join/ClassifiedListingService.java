package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.ClassifiedListingEntity;
import com.prosilion.nostrrelay.entity.join.ClassifiedListingEntityEventEntity;
import com.prosilion.nostrrelay.repository.ClassifiedListingEntityRepository;
import com.prosilion.nostrrelay.repository.join.ClassifiedListingEntityEventEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Service
public class ClassifiedListingService {
  private final ClassifiedListingEntityRepository classifiedListingEntityRepository;
  private final ClassifiedListingEntityEventEntityRepository join;

  public ClassifiedListingService() {
    classifiedListingEntityRepository = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEntityRepository.class);
    join = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEntityEventEntityRepository.class);
  }

  @Transactional
  public Long save(ClassifiedListing classifiedListing, Long eventId) throws InvocationTargetException, IllegalAccessException {
    return saveJoin(eventId, saveClassifiedListing(classifiedListing).getId()).getId();
  }

  private ClassifiedListingEntity saveClassifiedListing(ClassifiedListing classifiedListing) {
    return Optional.of(classifiedListingEntityRepository.save(
            new ClassifiedListingEntity(
                classifiedListing.getTitle(),
                classifiedListing.getSummary(),
                classifiedListing.getLocation(),
                classifiedListing.getPublishedAt())))
        .orElseThrow(NoResultException::new);
  }

  private ClassifiedListingEntityEventEntity saveJoin(Long eventId, Long classifiedListingId) {
    return Optional.of(join.save(new ClassifiedListingEntityEventEntity(eventId, classifiedListingId))).orElseThrow(NoResultException::new);
  }

  public ClassifiedListingEntity findById(Long id) {
    return classifiedListingEntityRepository.findById(id).get();
  }
}
