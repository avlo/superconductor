package com.prosilion.superconductor.service.event.join.standard;

import com.prosilion.superconductor.dto.standard.StandardTagDto;
import com.prosilion.superconductor.entity.join.standard.EventEntityStandardTagEntity;
import com.prosilion.superconductor.entity.standard.StandardTagEntity;
import com.prosilion.superconductor.repository.standard.StandardTagEntityRepository;
import com.prosilion.superconductor.repository.join.standard.EventEntityStandardTagEntityRepository;
import com.prosilion.superconductor.dto.standard.StandardTagDtoFactory;
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
import java.util.Set;
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

    fullMap.forEach((character, combo) -> {
      List<T> allByEventId = getAllByEventId(eventId, combo);
      List<T> list = allByEventId.stream().filter(Objects::nonNull).toList();
      List<T> put = joinMatches.put(character, list);
    });

    Stream<List<U>> listStream = joinMatches.entrySet().stream().map(this::getAllById);
    List<U> list = listStream.flatMap(Collection::stream).toList();
    return list;
  }

  private List<T> getAllByEventId(Long eventId, ComboMap combo) {
    EventEntityStandardTagEntityRepository<T> joinMap = combo.getJoinMap();
    Character code = joinMap.getCode();

    Set<Long> singleton = Collections.singleton(eventId);
    List<T> all = joinMap.findAll();
    List<T> findAllByEventId = joinMap.findAllById(singleton);
    List<T> getAllByEventId = joinMap.getAllByEventId(eventId);
    T referenceById = joinMap.getReferenceById(eventId);

    return getAllByEventId;
  }

  private @NotNull List<U> getAllById(Entry<Character, List<T>> joinEntry) {
    List<Long> lookupIds = getLookupIds(joinEntry);
    StandardTagEntityRepository<U> repoMap = getRepoMap(joinEntry);
    List<U> allById = repoMap.findAllById(lookupIds);
    return allById;
  }

  private List<Long> getLookupIds(Entry<Character, List<T>> joinEntry) {
    Function<T, Long> getLookupId = T::getLookupId;
    List<T> value = joinEntry.getValue();
    return value.stream().map(getLookupId).toList();
  }

  private StandardTagEntityRepository<U> getRepoMap(Entry<Character, List<T>> joinEntry) {
    Character key = joinEntry.getKey();
    ComboMap comboMap = fullMap.get(key);
    StandardTagEntityRepository<U> repoMap = comboMap.getRepoMap();
    return repoMap;
  }

  public void saveStandardTags(GenericEvent event, Long id) {
    List<BaseTag> eventStandardTags = getRelevantTags(event);
    List<StandardTagDto> dtos = createDtos(eventStandardTags);
    saveTags(dtos, id);
  }

  public List<BaseTag> getRelevantTags(GenericEvent event) {
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
    return standardTagEntityRepositoryMap.get(tag.getCode()).save(tag.convertDtoToEntity());
  }

  //  T extends EventEntityStandardTagEntity, U extends StandardTagEntity
  private void saveJoins(U tag, Long id) {
    T entity = standardTagDtoFactory.createEntity(tag, id);
    Character code = tag.getCode();
    EventEntityStandardTagEntityRepository<T> repo = joins.get(code);
    repo.save(entity);
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