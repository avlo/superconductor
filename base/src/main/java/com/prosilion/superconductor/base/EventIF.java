//package com.prosilion.superconductor.base;
//
//import com.prosilion.nostr.crypto.NostrUtil;
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.GenericEventKind;
//import com.prosilion.nostr.event.GenericEventKindIF;
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.nostr.user.Signature;
//import java.io.Serializable;
//import java.util.List;
//
//public interface EventIF<T> extends Serializable {
//  default GenericEventKindIF convertEntityToDto() {
//    byte[] rawData = NostrUtil.hex128ToBytes(getSignature());
//    final Signature signature = new Signature();
//    signature.setRawData(rawData);
//    return new GenericEventKind(
//        getEventIdString(),
//        new PublicKey(getPubKey()),
//        getCreatedAt(),
//        Kind.valueOf(getKind()),
//        getTags(),
//        getContent(),
//        signature);
//  }
//
//  String getEventIdString();
//
//  String getPubKey();
//
//  Integer getKind();
//
//  Long getCreatedAt();
//
//  String getContent();
//
//  List<BaseTag> getTags();
//
//  String getSignature();
//
////  void setEventIdString(String eventIdString);
////
////  void setPubKey(String pubKey);
////
////  void setKind(Integer kind);
////
////  void setCreatedAt(Long createdAt);
////
////  void setContent(String content);
////
//  void setTags(List<BaseTag> tags);
////
////  void setSignature(String signature);
//}
