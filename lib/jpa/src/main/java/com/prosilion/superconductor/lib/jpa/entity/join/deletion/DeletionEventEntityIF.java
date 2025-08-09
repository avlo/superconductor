package com.prosilion.superconductor.lib.jpa.entity.join.deletion;

import com.prosilion.superconductor.base.DeletionEventIF;

public interface DeletionEventEntityIF extends DeletionEventIF<Long> {
  Long getId();
  Long getEventId();
}
