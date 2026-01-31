//package com.prosilion.superconductor.base.service.event.service;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.event.GenericEventRecord;
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.nostr.user.Signature;
//import com.prosilion.superconductor.base.service.event.type.KindTypeIF;
//import java.util.List;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//
//@Slf4j
//public record GenericEventKindType(
//    @NonNull @JsonIgnore GenericEventRecord genericEventRecord,
//    @NonNull @Getter @EqualsAndHashCode.Exclude @JsonIgnore KindTypeIF kindType) implements GenericEventKindTypeIF {
//
//  @Override
//  public String getId() {
//    return genericEventRecord.getId();
//  }
//
//  @Override
//  @JsonProperty("pubkey")
//  public PublicKey getPublicKey() {
//    return genericEventRecord.getPublicKey();
//  }
//
//  @Override
//  @JsonProperty("created_at")
//  public Long getCreatedAt() {
//    return genericEventRecord.getCreatedAt();
//  }
//
//  @Override
//  public Kind getKind() {
//    return genericEventRecord.getKind();
//  }
//
//  @Override
//  public List<BaseTag> getTags() {
//    return genericEventRecord.getTags();
//  }
//
//  @Override
//  public String getContent() {
//    return genericEventRecord.getContent();
//  }
//
//  @Override
//  @JsonProperty("sig")
//  public Signature getSignature() {
//    return genericEventRecord.getSignature();
//  }
//
//  @Override
//  public String toBech32() {
//    return genericEventRecord.toBech32();
//  }
//}
