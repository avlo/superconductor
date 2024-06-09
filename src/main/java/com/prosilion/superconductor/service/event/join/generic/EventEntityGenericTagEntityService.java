package com.prosilion.superconductor.service.event.join.generic;

import com.prosilion.superconductor.dto.generic.GenericTagDto;
import com.prosilion.superconductor.dto.generic.GenericTagDtoFactory;
import com.prosilion.superconductor.entity.generic.GenericTagEntity;
import com.prosilion.superconductor.entity.join.generic.EventEntityGenericTagEntity;
import com.prosilion.superconductor.repository.generic.GenericTagEntityRepository;
import com.prosilion.superconductor.repository.join.generic.EventEntityGenericTagEntityRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
public class EventEntityGenericTagEntityService<T extends EventEntityGenericTagEntity, U extends GenericTagEntity> {
  private final Map<Character, GenericTagEntityRepository<U>> genericTagEntityRepositoryMap;
  private final Map<Character, EventEntityGenericTagEntityRepository<T>> joins;
  private final GenericTagDtoFactory<T> genericTagDtoFactory;
  private final Map<Character, ComboMap> fullMap;

  @Autowired
  public EventEntityGenericTagEntityService(
      List<GenericTagEntityRepository<U>> repositoriesList,
      List<EventEntityGenericTagEntityRepository<T>> joinList,
      GenericTagDtoFactory<T> genericTagDtoFactory) {

    this.genericTagDtoFactory = genericTagDtoFactory;

    this.genericTagEntityRepositoryMap = repositoriesList.stream().collect(
        Collectors.toMap(
            GenericTagEntityRepository<U>::getCode,
            Function.identity()));
    this.joins = joinList.stream().collect(
        Collectors.toMap(
            EventEntityGenericTagEntityRepository<T>::getCode,
            Function.identity()));

    fullMap = new HashMap<Character, ComboMap>();

    //TODO: before doing below, check to see lists are same size and have same map characters

    repositoriesList.forEach(repo -> {
      ComboMap cmap = new ComboMap();
      cmap.setRepoMap(repo);
      fullMap.put(repo.getCode(), cmap);
    });

    joinList.forEach(join -> fullMap.get(join.getCode()).setJoinMap(join));
  }

  public List<U> getTags(Long eventId) {
    Map<Character, List<T>> joinMatches = new HashMap<>();

    fullMap.forEach((character, combo) ->
        joinMatches.put(character, getAllByEventId(eventId, combo).stream().filter(Objects::nonNull).toList()));

    return joinMatches.entrySet().stream().map(
        this::getAllById).flatMap(Collection::stream).toList();
  }

  private List<T> getAllByEventId(Long eventId, ComboMap combo) {
    return combo.getJoinMap().findAllById(
        Collections.singleton(eventId));
  }

  private @NotNull List<U> getAllById(Entry<Character, List<T>> joinEntry) {
    return getRepoMap(joinEntry)
        .findAllById(
            getLookupIds(joinEntry));
  }

  private List<Long> getLookupIds(Entry<Character, List<T>> joinEntry) {
    return joinEntry.getValue().stream().map(T::getLookupId).toList();
  }

  private GenericTagEntityRepository<U> getRepoMap(Entry<Character, List<T>> joinEntry) {
    return fullMap.get(
            joinEntry.getKey())
        .getRepoMap();
  }

  public void saveGenericTags(GenericEvent event) {
    log.info("saving generic tags for event: [{}]", event.getId());
    saveTags(
        createDtos(
            selectReposByRelevantTags(
                getRelevantTags(event))));
  }

  private List<BaseTag> getRelevantTags(GenericEvent event) {
    List<BaseTag> list = Optional.of(event.getTags().stream()).orElse(Stream.empty())
        .filter(baseTag -> (baseTag.getCode().length() == 1))
        .filter(baseTag -> !List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
    return list;
  }

  private List<Map<Character, String>> selectReposByRelevantTags(List<BaseTag> baseTags) {
    List<Map<Character, String>> list = baseTags.stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(
            this::containsKey)
        .map(
            this::mapAtts)
        .toList();
    return list;
  }

  private boolean containsKey(GenericTag tag) {
    char key = tag.getCode().charAt(0);
    return genericTagEntityRepositoryMap.containsKey(key);
  }

  private Map<Character, String> mapAtts(GenericTag gTag) {
    return Map.of(gTag.getCode().charAt(0), gTag.getAttributes().get(0).getValue().toString());
  }

  private List<GenericTagDto> createDtos(List<Map<Character, String>> list) {
    return list.stream().flatMap(tag -> tag.entrySet().stream()).map(this::createDto).toList();
  }

  private GenericTagDto createDto(Map.Entry<Character, String> s) {
    return genericTagDtoFactory.createDto(s.getKey(), s.getValue());
  }

  private void saveTags(List<GenericTagDto> tags) {
    tags.stream().map(
            this::saveTag)
        .forEach(
            this::saveJoins);
  }

  private U saveTag(GenericTagDto tag) {
    return genericTagEntityRepositoryMap.get(tag.getCode()).save(tag.convertDtoToEntity());
  }

  private void saveJoins(U tag) {
    joins.get(
            tag.getCode())
        .save(
            genericTagDtoFactory.createEntity(tag.getCode(), tag.getId(), tag.getId()));
  }

  @Setter
  @Getter
  private class ComboMap {
    GenericTagEntityRepository<U> repoMap;
    EventEntityGenericTagEntityRepository<T> joinMap;
  }
}