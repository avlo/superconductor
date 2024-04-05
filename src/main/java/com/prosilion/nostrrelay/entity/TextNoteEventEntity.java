package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.event.TextNoteEventDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "text_note_event")
public class TextNoteEventEntity implements EventEntityDecorator {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Lob
  private String content;

  // List<BaseTag> to be stored in their own join table ENTITY_TAGS
  // private List<BaseTag> tags;

  public TextNoteEventDto convertEntityToDto(PublicKey publicKey, List<BaseTag> baseTags) throws InvocationTargetException, IllegalAccessException {
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(publicKey, baseTags, content);
    BeanUtils.copyProperty(textNoteEventDto, "content", this.getContent());
    return textNoteEventDto;
  }
}
