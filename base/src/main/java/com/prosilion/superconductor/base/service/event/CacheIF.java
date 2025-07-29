package com.prosilion.superconductor.base.service.event;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.DeletionEntityIF;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface CacheIF {

  //  List<? extends EventIF>         getAll();
  List<? extends EventIF> getAll();


  Optional<? extends EventIF> getByEventIdString(@NonNull String eventId);


  void deleteEventEntity(@NonNull EventIF eventIF);


  List<? extends EventIF> getEventsByKind(@NonNull Kind kind);

  void saveEventEntityOrDocument(EventIF event);

//  <T> 
//  Map<Kind, 
//      Map<? extends T,
//          ? extends EventIF>>     getAllMappedByKind();

  Map<Kind, Map<?, ? extends EventIF>> getAllMappedByKind();


  List<DeletionEntityIF> getAllDeletionEventEntities();
}
