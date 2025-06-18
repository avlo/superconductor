//package com.prosilion.superconductor.dto.standard;
//
//import com.prosilion.superconductor.dto.AbstractTagDto;
//import com.prosilion.superconductor.entity.standard.VoteTagEntity;
//import org.springframework.lang.NonNull;
//import com.prosilion.nostr.tag.VoteTag;
//
//public class VoteTagDto implements AbstractTagDto {
//  private final VoteTag voteTag;
//
//  public VoteTagDto(@NonNull VoteTag voteTag) {
//    this.voteTag = voteTag;
//  }
//
//  @Override
//  public String getCode() {
//    return voteTag.getCode();
//  }
//
//  @Override
//  public VoteTagEntity convertDtoToEntity() {
//    return new VoteTagEntity(voteTag);
//  }
//}
