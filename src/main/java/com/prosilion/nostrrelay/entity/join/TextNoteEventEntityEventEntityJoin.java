package com.prosilion.nostrrelay.entity.join;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "text_note_event-event-join")
public class TextNoteEventEntityEventEntityJoin implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long textNoteEventId;
  private Long eventId;

  public TextNoteEventEntityEventEntityJoin(Long textNoteEventId, Long eventId) {
    this.textNoteEventId = textNoteEventId;
    this.eventId = eventId;
  }
}