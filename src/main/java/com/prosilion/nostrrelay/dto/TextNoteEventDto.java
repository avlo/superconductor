package com.prosilion.nostrrelay.dto;

import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.TextNoteEvent;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
public class TextNoteEventDto extends TextNoteEvent {
  public TextNoteEventDto(PublicKey pubKey, List<BaseTag> tags, String content) {
    super(pubKey, tags, content);
  }

  public TextNoteEventEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    TextNoteEventEntity textNoteEventEntity = new TextNoteEventEntity();
    BeanUtils.copyProperties(textNoteEventEntity, this);
    return textNoteEventEntity;
  }
}
