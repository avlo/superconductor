package com.prosilion.nostrrelay.entity;

import com.prosilion.nostrrelay.dto.event.ClassifiedListingEventDto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import nostr.base.PublicKey;
import nostr.event.BaseTag;
import nostr.event.impl.ClassifiedListingEvent.ClassifiedListing;
import nostr.event.impl.GenericTag;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "classified_listing_event")
public class ClassifiedListingEventEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Lob
  private String content;

  // List<BaseTag> to be stored in their own join table ENTITY_TAGS
  // private List<BaseTag> tags;

  private String signature;
  private String eventId;
  private String pubKey;
  private Integer kind;
  private Integer nip;
  private Long createdAt;
  public ClassifiedListingEventDto convertClassifiedListingEventEntityToDto(ClassifiedListing classifiedListing) {
    List<BaseTag> tags = List.of(
        GenericTag.create("e", 1, "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"),
        GenericTag.create("p", 1, "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984")
    );
    ClassifiedListingEventDto classifiedListingEventDto = new ClassifiedListingEventDto(
        new PublicKey(getPubKey()),
        tags,
        getContent(),
        classifiedListing);
    BeanUtils.copyProperties(classifiedListingEventDto, this);
    return classifiedListingEventDto;
  }
}
