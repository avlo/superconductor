package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.dto.TagModule;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntityRxR;
import com.prosilion.superconductor.entity.standard.StandardTagEntityRxR;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepositoryRxR;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepositoryRxR;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TagEntitiesService<
    P extends BaseTag,
    Q extends StandardTagEntityRepositoryRxR<R>,
    R extends StandardTagEntityRxR,
    S extends EventEntityStandardTagEntityRxR,
    U extends EventEntityStandardTagEntityRepositoryRxR<S>> {
  private final List<TagModule<P, Q, R, S, U>> tagModules;

  @Autowired
  public TagEntitiesService(List<TagModule<P, Q, R, S, U>> tagModules) {
    this.tagModules = tagModules;
  }

  public List<StandardTagEntityRxR> getTags(Long eventId) {
    return tagModules.parallelStream().map(tagModule ->
            tagModule.getTags(eventId))
        .flatMap(List::stream).collect(Collectors.toList());
  }

  public void saveTags(Long eventId, List<P> baseTags) {
    tagModules.parallelStream().forEach(module ->
        baseTags.stream().filter(tags ->
                tags.getCode().equalsIgnoreCase(module.getCode()))
            .forEach(tag ->
                module.saveTag(eventId, tag)));
  }
}