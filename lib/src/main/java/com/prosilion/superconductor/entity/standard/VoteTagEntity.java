//package com.prosilion.superconductor.entity.standard;
//
//import com.prosilion.superconductor.dto.AbstractTagDto;
//import com.prosilion.superconductor.dto.standard.VoteTagDto;
//import com.prosilion.superconductor.entity.AbstractTagEntity;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//import jakarta.persistence.Transient;
//import java.util.List;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.lang.NonNull;
//import lombok.Setter;
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.tag.VoteTag;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "vote_tag")
//public class VoteTagEntity extends AbstractTagEntity {
//  private Integer vote;
//
//  public VoteTagEntity(@NonNull VoteTag voteTag) {
//    super("v");
//    this.vote = voteTag.getVote();
//  }
//
//  @Override
//  @Transient
//  public BaseTag getAsBaseTag() {
//    return new VoteTag(vote);
//  }
//
//  @Override
//  public AbstractTagDto convertEntityToDto() {
//    return new VoteTagDto(new VoteTag(vote));
//  }
//
//  @Override
//  @Transient
//  public List<String> get() {
//    return List.of(String.valueOf(vote));
//  }
//}
