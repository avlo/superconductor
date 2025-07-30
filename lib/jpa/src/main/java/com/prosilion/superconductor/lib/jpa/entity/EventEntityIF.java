package com.prosilion.superconductor.lib.jpa.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import java.util.List;

public interface EventEntityIF extends EventIF {
  Long getUid();
  void setUid(Long uid);

  List<BaseTag> getTags();
  void setTags(List<BaseTag> tags);

  String getId();
  void setId(String id);

  PublicKey getPublicKey();
  void setPublicKey(PublicKey publicKey);

  Long getCreatedAt();
  void setCreatedAt(Long createdAt);

  Kind getKind();
  void setKind(Kind kind);

  String getContent();
  void setContent(String content);

  Signature getSignature();
  void setSignature(Signature signature);
}
