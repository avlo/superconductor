package com.prosilion.superconductor.lib.jpa.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import java.util.List;

public interface EventEntityIF extends EventIF {
//  default GenericEventKindIF convertEntityToGenericEventKindIF() {
//    final Signature signature = new Signature();
//    signature.setRawData(NostrUtil.hex128ToBytes(getSignature().toString()));
//    return new GenericEventKind(
//        getId(),
//        getPublicKey(),
//        getCreatedAt(),
//        getKind(),
//        getTags(),
//        getContent(),
//        signature);
//  }

  Long getUid();

  void setUid(Long uid);

  void setTags(List<BaseTag> tags);

  String getId();

  void setId(String id);

  PublicKey getPublicKey();

  void setPublicKey(PublicKey publicKey);

  Long getCreatedAt();

  void setCreatedAt(Long createdAt);

  Kind getKind();

  void setKind(Kind kind);

  List<BaseTag> getTags();

  String getContent();

  void setContent(String content);

  Signature getSignature();

  void setSignature(Signature signature);
}
