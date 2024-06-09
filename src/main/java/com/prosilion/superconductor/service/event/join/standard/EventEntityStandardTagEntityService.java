package com.prosilion.superconductor.service.event.join.standard;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.dto.standard.StandardTagDtoFactory;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepository;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
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
public class EventEntityStandardTagEntityService<T extends EventEntityStandardTagEntity, U extends StandardTagEntity> {
  private final Map<Character, StandardTagEntityRepository<U>> standardTagEntityRepositoryMap;
  private final Map<Character, EventEntityStandardTagEntityRepository<T>> joins;
  private final StandardTagDtoFactory<T> standardTagDtoFactory;
  private final Map<Character, ComboMap> fullMap;


  @Autowired
  public EventEntityStandardTagEntityService(
      List<StandardTagEntityRepository<U>> repositoriesList,
      List<EventEntityStandardTagEntityRepository<T>> joinList,
      StandardTagDtoFactory<T> standardTagDtoFactory) {

    this.standardTagDtoFactory = standardTagDtoFactory;

    this.standardTagEntityRepositoryMap = repositoriesList.stream().collect(
        Collectors.toMap(
            StandardTagEntityRepository<U>::getCode,
            Function.identity()));

    this.joins = joinList.stream().collect(
        Collectors.toMap(
            EventEntityStandardTagEntityRepository<T>::getCode,
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

  private StandardTagEntityRepository<U> getRepoMap(Entry<Character, List<T>> joinEntry) {
    return fullMap.get(
            joinEntry.getKey())
        .getRepoMap();
  }

  public void saveStandardTags(GenericEvent event, Long id) {
    saveTags(
        createDtos(
            getRelevantTags(event)), id);
  }

  private List<BaseTag> getRelevantTags(GenericEvent event) {
    return Optional.of(event.getTags().stream()).orElse(Stream.empty())
        .filter(Objects::nonNull)
        .filter(baseTag -> List.of("a", "p", "e").contains(baseTag.getCode()))
        .toList();
  }

  private void saveTags(List<StandardTagDto> tags, Long id) {
    tags.stream().map(
            this::saveTag)
        .forEach(tag -> saveJoins(tag, id));
  }

  private U saveTag(StandardTagDto tag) {
    return standardTagEntityRepositoryMap
        .get(tag.getCode())
        .save(tag.convertDtoToEntity());
  }

  private void saveJoins(U tag, Long id) {
    joins.get(tag.getCode())
        .save(standardTagDtoFactory.createEntity(tag, id));
  }

  private List<StandardTagDto> createDtos(List<BaseTag> characterBaseTagListMap) {
    return characterBaseTagListMap.stream().map(standardTagDtoFactory::createDto).toList();
  }

  @Setter
  @Getter
  private class ComboMap {
    StandardTagEntityRepository<U> repoMap;
    EventEntityStandardTagEntityRepository<T> joinMap;
  }
}