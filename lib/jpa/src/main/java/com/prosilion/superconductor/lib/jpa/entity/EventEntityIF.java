package com.prosilion.superconductor.lib.jpa.entity;

import com.prosilion.superconductor.base.EventIF;

public interface EventEntityIF extends EventIF {
  Long getId();
  void setId(Long id);
}
