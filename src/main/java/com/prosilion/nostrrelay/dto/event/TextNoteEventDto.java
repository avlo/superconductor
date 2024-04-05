package com.prosilion.nostrrelay.dto.event;

import com.prosilion.nostrrelay.entity.EventDtoDecorator;
import com.prosilion.nostrrelay.entity.TextNoteEventEntity;
import lombok.Getter;
import lombok.Setter;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.TextNoteEvent;
import org.apache.commons.beanutils.BeanUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Setter
@Getter
public class TextNoteEventDto extends TextNoteEvent implements EventDtoDecorator {
  private final String content;

  public TextNoteEventDto(PublicKey pubKey, List<BaseTag> tags, String content) {
    super(pubKey, tags, content);
    this.content = content;
  }

  public @NotNull TextNoteEventEntity convertDtoToEntity() throws InvocationTargetException, IllegalAccessException {
    TextNoteEventEntity textNoteEventEntity = new TextNoteEventEntity();
    BeanUtils.copyProperty(textNoteEventEntity, "content", this.getContent());
    return textNoteEventEntity;
  }
}
