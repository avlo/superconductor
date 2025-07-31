package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocument;
import com.prosilion.superconductor.lib.redis.document.DeletionEventDocumentRedisIF;
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
  private final DeletionEventDocumentRepository repo;
  private final Map<String, TagInterceptor<BaseTag, RedisBaseTagIF>> interceptors;

  public DeletionEventDocumentService(
      @NonNull DeletionEventDocumentRepository deletionEventDocumentRepository,
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
    return repo.findByEventIdCustom(eventIdString).map(this::revertInterceptor);
  }

//  public List<DeletionEventDocumentRedisIF> getEventsByKind(@NonNull Kind kind) {
//    return repo
//        .findAllByKind(kind.getValue()).stream()
//        .map(this::revertInterceptor)
//        .toList();
//  }

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
        .findAllCustom().stream()
        .map(this::revertInterceptor)
        .toList();
  }

  protected void deleteEventEntity(@NonNull String eventId) {
//    Optional<DeletionEventDocumentRedisIF> byEventIdString = repo.findByEventIdCustom(eventId);
    Optional<DeletionEventDocument> byEventId = repo.findByEventId(eventId);
    Optional<DeletionEventDocument> deletionEventDocument = byEventId.map(repo::save).map(DeletionEventDocument::getEventId).map(DeletionEventDocument::of);
    log.debug("DeletionEventDocumentService deletionEventDocument: {}", deletionEventDocument);
  }

  private DeletionEventDocumentRedisIF convertDtoToDocument(EventIF dto) {
    return processInterceptors(dto);
  }

  private DeletionEventDocumentRedisIF processInterceptors(EventIF dto) {
    DeletionEventDocument eventDocument = DeletionEventDocument.of(
        dto.getId());

    return eventDocument;
  }

  private DeletionEventDocumentRedisIF revertInterceptor(DeletionEventDocumentRedisIF documentToRevert) {
    DeletionEventDocument revertedDocument = DeletionEventDocument.of(
        documentToRevert.getId());

    return revertedDocument;
  }
}
