package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.superconductor.base.service.event.type.KindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF extends EventKindServiceIF {
  List<KindTypeIF> getKindTypes();
}
