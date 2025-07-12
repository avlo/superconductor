package com.prosilion.superconductor.base;

import java.io.Serializable;

public interface DeletionEventEntityIF extends Serializable {
  Long getId();

  Long getEventId();
}
