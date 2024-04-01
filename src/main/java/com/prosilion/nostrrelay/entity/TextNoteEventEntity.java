package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.TextNoteEventDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.GenericTag;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "text_note_event")
public class TextNoteEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "native")
  @Column
  private long id;

  @Basic
  @Column
  private String pubKey;

  @Basic
  @Column
  private Long createdAt;

  @Basic
  @Column
  private Integer kind;

  // @Key
  // @EqualsAndHashCode.Exclude
  // @JsonProperty("tags")
  // private List<BaseTag> tags;

  @Lob
  @Column
  private String content;

  @Basic
  @Column
  private String signature;

  @Basic
  @Column
  private Integer nip;

  public TextNoteEventDto convertEntityToDto() {
    List<BaseTag> tags = List.of(
        GenericTag.create("e", 1, "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"),
        GenericTag.create("p", 1, "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")
    );
    TextNoteEventDto textNoteEventDto = new TextNoteEventDto(new PublicKey(pubKey), tags, content);
    BeanUtils.copyProperties(textNoteEventDto, this);
    return textNoteEventDto;
  }
}