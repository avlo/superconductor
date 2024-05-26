package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.entity.join.classified.ZapRequestEventEntityEventEntity;
import com.prosilion.superconductor.repository.join.zaprequest.ZapRequestEventEntityEventEntityRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
// TODO: refactor this & similiar classes using common pattern
public class ZapRequestEventEntityEventEntityService {
  private final ZapRequestEventEntityEventEntityRepository join;

  public ZapRequestEventEntityEventEntityService(ZapRequestEventEntityEventEntityRepository join) {
    this.join = join;
  }

  public Long save(Long eventId, Long zapRequestEventId) {
    return Optional.of(join.save(new ZapRequestEventEntityEventEntity(eventId, zapRequestEventId))).orElseThrow(NoResultException::new).getId();
  }
}
