package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.tag.BaseTag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import com.prosilion.superconductor.lib.jpa.plugin.tag.TagPlugin;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConcreteTagEntitiesService<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>,
    R extends AbstractTagEntity,
    S extends EventEntityAbstractEntity,
    T extends EventEntityAbstractTagEntityRepository<S>> {
  private final List<TagPlugin<P, Q, R, S, T>> tagPlugins;

  @Autowired
  public ConcreteTagEntitiesService(List<TagPlugin<P, Q, R, S, T>> tagPlugins) {
   this.tagPlugins = tagPlugins;
  }

  public List<AbstractTagEntity> getTags(@NonNull Long eventId) {
    return tagPlugins.stream().map(tagModule ->
            tagModule.getTags(eventId))
        .flatMap(List::stream).distinct().collect(Collectors.toList());
  }

  public void saveTags(@NonNull Long eventId, @NonNull List<P> baseTags) {
    tagPlugins.forEach(module ->
        baseTags.stream().filter(tags ->
                tags.getCode().equalsIgnoreCase(module.getCode()))
            .forEach(tag ->
                module.saveTag(eventId, tag)));
  }
}
