package com.prosilion.superconductor.service.event.join;

import com.prosilion.superconductor.dto.SubjectTagDto;
import com.prosilion.superconductor.entity.join.EventEntitySubjectTagEntity;
import com.prosilion.superconductor.repository.SubjectTagEntityRepository;
import com.prosilion.superconductor.repository.join.EventEntitySubjectTagEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EventEntitySubjectTagEntityService {
  private final SubjectTagEntityRepository subjectTagEntityRepository;
  private final EventEntitySubjectTagEntityRepository join;

  @Autowired
  public EventEntitySubjectTagEntityService(SubjectTagEntityRepository subjectTagEntityRepository, EventEntitySubjectTagEntityRepository join) {
    this.subjectTagEntityRepository = subjectTagEntityRepository;
    this.join = join;
  }

  public void saveSubjectTag(SubjectTagDto subjectTag, Long id) {
    saveJoin(id, saveTag(subjectTag));
  }

  private Long saveTag(SubjectTagDto tag) {
    return subjectTagEntityRepository.save(tag.convertDtoToEntity()).getId();
  }

  private void saveJoin(Long eventId, Long tagId) {
    join.save(new EventEntitySubjectTagEntity(eventId, tagId));
  }
}