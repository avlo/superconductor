package com.prosilion.superconductor.lib.jpa.dto.deletion;

import com.prosilion.superconductor.base.DeletionEntityIF;

public interface DeletionEventEntityJpaIF extends DeletionEntityIF<Long> {
  Long getId();
}
