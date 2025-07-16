package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.EventIF;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

@Data
//@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@Document
public class EventDocument implements EventIF {

  @Id
  @NonNull
  private String eventIdString;

  @NonNull
  @Indexed
  private Integer kind;

  @NonNull
  @Indexed
  private String pubKey;

  @NonNull
  private Long createdAt;

  @NonNull
  private String content;

  @Indexed
  @Searchable
  private List<BaseTag> tags = new ArrayList<>();

  @NonNull
  private String signature;
}
