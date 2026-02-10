package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractJpaEntity;
import com.prosilion.superconductor.lib.jpa.plugin.tag.TagPlugin;
import com.prosilion.superconductor.lib.jpa.repository.AbstractTagJpaEntityRepository;
import com.prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagJpaEntityRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConcreteTagEntitiesService<
    P extends BaseTag,
    Q extends AbstractTagJpaEntityRepository<R>,
    R extends AbstractTagJpaEntity,
    S extends EventEntityAbstractJpaEntity,
    T extends EventEntityAbstractTagJpaEntityRepository<S>> {
  private final List<TagPlugin<P, Q, R, S, T>> tagPlugins;

  @Autowired
  public ConcreteTagEntitiesService(List<TagPlugin<P, Q, R, S, T>> tagPlugins) {
    this.tagPlugins = tagPlugins;
  }

  public List<AbstractTagJpaEntity> getTags(@NonNull Long eventId) {
    return tagPlugins.stream().map(tagModule ->
            tagModule.getTags(eventId))
        .flatMap(List::stream)
        .distinct()
        .collect(Collectors.toList());
  }

  public Map<Long, List<AbstractTagJpaEntity>> getTagsByEventIds(@NonNull Collection<Long> eventIds) {
    Map<Long, List<AbstractTagJpaEntity>> result = new HashMap<>();
    tagPlugins.forEach(tagPlugin ->
        tagPlugin.getTagsByEventIds(eventIds)
            .forEach((eventId, tags) ->
            result.computeIfAbsent(eventId, k -> new ArrayList<>()).addAll(tags)));
    return result;
  }

  public void saveTags(@NonNull Long eventId, @NonNull List<P> baseTags) {
    tagPlugins.forEach(tagPlugin ->
        baseTags.stream().filter(tags ->
                tags.getCode().equalsIgnoreCase(tagPlugin.getCode()))
            .forEach(tag ->
                tagPlugin.saveTag(eventId, tag)));
  }
}
