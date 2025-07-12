package prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.tag.BaseTag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import prosilion.superconductor.lib.jpa.entity.AbstractTagEntity;
import prosilion.superconductor.lib.jpa.entity.join.EventEntityAbstractEntity;
import prosilion.superconductor.lib.jpa.plugin.tag.TagPlugin;
import prosilion.superconductor.lib.jpa.repository.AbstractTagEntityRepository;
import prosilion.superconductor.lib.jpa.repository.join.EventEntityAbstractTagEntityRepository;

@Slf4j
public class ConcreteTagEntitiesService<
    P extends BaseTag,
    Q extends AbstractTagEntityRepository<R>,
    R extends AbstractTagEntity,
    S extends EventEntityAbstractEntity,
    T extends EventEntityAbstractTagEntityRepository<S>> {
  private final List<TagPlugin<P, Q, R, S, T>> tagPlugins;

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
