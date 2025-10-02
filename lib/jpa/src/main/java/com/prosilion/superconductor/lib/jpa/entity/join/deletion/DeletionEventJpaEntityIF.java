package com.prosilion.superconductor.lib.jpa.entity.join.deletion;

import com.prosilion.superconductor.base.DeletionEventIF;

public interface DeletionEventJpaEntityIF extends DeletionEventIF<Long> {
  Long getId();
  Long getEventId();
}
