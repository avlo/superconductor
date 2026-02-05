package com.prosilion.superconductor.base.service.event.kind.type;

import com.prosilion.superconductor.base.service.event.kind.EventKindServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.KindTypeIF;
import java.util.List;

public interface EventKindTypeServiceIF extends EventKindServiceIF {
  List<KindTypeIF> getKindTypes();
}
