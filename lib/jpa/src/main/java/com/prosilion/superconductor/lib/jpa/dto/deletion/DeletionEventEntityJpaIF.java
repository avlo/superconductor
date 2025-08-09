package com.prosilion.superconductor.lib.jpa.dto.deletion;

import com.prosilion.superconductor.base.DeletionEventIF;

public interface DeletionEventEntityJpaIF extends DeletionEventIF<Long> {
  Long getId();
  Long getEventId();
}
