package com.prosilion.nostrrelay.service.event.join;

import com.prosilion.nostrrelay.config.ApplicationContextProvider;
import com.prosilion.nostrrelay.entity.join.ClassifiedListingEntityEventEntity;
import com.prosilion.nostrrelay.repository.join.ClassifiedListingEntityEventEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClassifiedListingEntityEventEntityService {
  private final ClassifiedListingEntityEventEntityRepository join;

  public ClassifiedListingEntityEventEntityService() {
    join = ApplicationContextProvider.getApplicationContext().getBean(ClassifiedListingEntityEventEntityRepository.class);
  }

  @Transactional
  public Long save(Long eventId, Long classifiedListingId) {
    return Optional.of(join.save(new ClassifiedListingEntityEventEntity(eventId, classifiedListingId))).orElseThrow(NoResultException::new).getId();
  }
}
