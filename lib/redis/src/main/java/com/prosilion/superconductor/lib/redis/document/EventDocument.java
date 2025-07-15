package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.superconductor.base.EventIF;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
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
//  @Searchable
  private List<BaseTag> tags = new ArrayList<>();

  @NonNull
  private String signature;
}
