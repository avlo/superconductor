//package com.prosilion.superconductor.service.event.join;
//
//import com.prosilion.superconductor.entity.SubjectTagEntityRxR;
//import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntityRxR;
//import com.prosilion.superconductor.repository.SubjectTagEntityRepositoryRxR;
//import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepositoryRxR;
//import com.prosilion.superconductor.service.event.join.standard.EventEntityStandardTagEntityServiceIFRxR;
//import jakarta.transaction.Transactional;
//import lombok.Getter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Getter
//@Service
//@Transactional
//public class EventEntitySubjectTagEntityServiceRxR<T extends SubjectTagEntityRxR, U extends EventEntitySubjectTagEntityRxR> implements EventEntityStandardTagEntityServiceIFRxR<T, U> {
//  private final SubjectTagEntityRepositoryRxR<T> subjectTagEntityRepository;
//  private final EventEntitySubjectTagEntityRepositoryRxR<U> join;
//  private final Class<T> clazz;
//
//  @Autowired
//  public EventEntitySubjectTagEntityServiceRxR(SubjectTagEntityRepositoryRxR<T> subjectTagEntityRepository, EventEntitySubjectTagEntityRepositoryRxR<U> join) {
//    this.subjectTagEntityRepository = subjectTagEntityRepository;
//    this.join = join;
//    this.clazz = (Class<T>) SubjectTagEntityRxR.class;
//  }
//
////  public void saveTags(GenericEvent event, Long id) {
////    Optional.of(event.getTags().stream()).orElse(Stream.empty())
////        .filter(baseTag -> "subject".equals(baseTag.getCode())).forEach(subjectTag ->
////            saveSubjectTag(new SubjectTagDtoRxR(((SubjectTag) subjectTag).getSubject()), id));
////  }
////
////  private void saveSubjectTag(SubjectTagDtoRxR subjectTag, Long id) {
////    saveJoin(id, saveTag(subjectTag));
////  }
////
////  private Long saveTag(SubjectTagDtoRxR tag) {
////    return subjectTagEntityRepository.save((T) tag.convertDtoToEntity()).getId();
////  }
////
////  private void saveJoin(Long eventId, Long tagId) {
////    join.save((U) new EventEntitySubjectTagEntityRxR(eventId, tagId));
////  }
//}