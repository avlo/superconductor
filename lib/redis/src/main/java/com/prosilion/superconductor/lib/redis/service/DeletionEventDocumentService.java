package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
import com.prosilion.superconductor.lib.redis.dto.GenericDocumentKindDto;
import com.prosilion.superconductor.lib.redis.interceptor.RedisBaseTagIF;
import com.prosilion.superconductor.lib.redis.interceptor.TagInterceptor;
import com.prosilion.superconductor.lib.redis.repository.DeletionEventDocumentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class DeletionEventDocumentService {
  private final DeletionEventDocumentRepository<DeletionEventDocumentRedisIF> repo;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public DeletionEventDocumentService(
      @NonNull DeletionEventDocumentRepository<DeletionEventDocumentRedisIF> deletionEventDocumentRepository,
      @NonNull List<TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors) {
    this.repo = deletionEventDocumentRepository;
    this.interceptors = interceptors.stream().collect(
        Collectors.toMap(
            TagInterceptor::getCode,
            Function.identity()));
    log.debug("Created EventDocumentService with interceptors:\n");
    interceptors.forEach(interceptor -> log.debug("  {}\n", interceptor));
  }

  public Optional<DeletionEventDocumentRedisIF> findByEventIdString(@NonNull String eventIdString) {
    return repo.findByEventIdString(eventIdString).map(this::revertInterceptor);
  }

  public List<DeletionEventDocumentRedisIF> getEventsByKind(@NonNull Kind kind) {
    return repo
        .findAllByKind(kind.getValue()).stream()
        .map(this::revertInterceptor)
        .toList();
  }

//  public Map<Kind, Map<String, DeletionEventDocumentRedisIF>> getAllMappedByKind() {
//    List<DeletionEventDocumentRedisIF> eventDocumentRepositoryAll = getAll();
//    return eventDocumentRepositoryAll
//        .stream()
//        .collect(
//            Collectors.groupingBy(
//                EventIF::getKind,
//                Collectors.toMap(
//                    DeletionEventDocumentRedisIF::getId,
//                    Function.identity(), (prev, next) -> next)));
//  }

  public List<DeletionEventDocumentRedisIF> getAll() {
    return repo
        .findAll().stream()
        .map(this::revertInterceptor)
        .toList();
  }

  public DeletionEventDocumentRedisIF saveEventDocument(@NonNull BaseEvent baseEvent) {
    return saveEventDocument(new GenericDocumentKindDto(baseEvent).convertDtoToDocument());
  }

  public DeletionEventDocumentRedisIF saveEventDocument(@NonNull EventIF eventDocument) {
    return saveEventDocument(convertDtoToDocument(eventDocument));
  }

  public DeletionEventDocumentRedisIF saveEventDocument(@NonNull DeletionEventDocumentRedisIF entity) {
    return repo.save(
        entity);
  }

  protected void deleteEventEntity(@NonNull String eventId) {
    Optional<DeletionEventDocumentRedisIF> byEventIdString = repo.findByEventIdString(eventId);
    byEventIdString.ifPresent(repo::delete);
  }

  private DeletionEventDocumentRedisIF convertDtoToDocument(EventIF dto) {
    return processInterceptors(dto);
  }

  private DeletionEventDocumentRedisIF processInterceptors(EventIF dto) {
    DeletionEventDocument eventDocument = DeletionEventDocument.of(
        dto.getEventId());

    return eventDocument;
  }

  private DeletionEventDocumentRedisIF revertInterceptor(DeletionEventDocumentRedisIF documentToRevert) {
    DeletionEventDocument revertedDocument = DeletionEventDocument.of(
        documentToRevert.getId());

    return revertedDocument;
  }
}
