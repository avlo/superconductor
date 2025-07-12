package com.prosilion.superconductor.base;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.BaseTag;
import java.util.List;

public interface EventEntityIF {
  <T extends GenericEventKindIF> T convertEntityToDto();

  long getId();

  String getEventIdString();

  String getPubKey();

  Integer getKind();

  Long getCreatedAt();

  String getContent();

  List<BaseTag> getTags();

  String getSignature();

  void setId(long id);

  void setEventIdString(String eventIdString);

  void setPubKey(String pubKey);

  void setKind(Integer kind);

  void setCreatedAt(Long createdAt);

  void setContent(String content);

  void setTags(List<BaseTag> tags);

  void setSignature(String signature);
}
