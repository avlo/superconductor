package com.prosilion.superconductor.service.event.join.classified;

import com.prosilion.superconductor.entity.join.classified.ClassifiedListingEntityEventEntity;
import com.prosilion.superconductor.repository.join.classified.ClassifiedListingEntityEventEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class ClassifiedListingEntityEventEntityService {
  private final ClassifiedListingEntityEventEntityRepository join;

  public ClassifiedListingEntityEventEntityService(ClassifiedListingEntityEventEntityRepository join) {
    this.join = join;
  }

  public Long save(Long eventId, Long classifiedListingId) {
    return Optional.of(join.save(new ClassifiedListingEntityEventEntity(eventId, classifiedListingId))).orElseThrow(NoResultException::new).getId();
  }
}