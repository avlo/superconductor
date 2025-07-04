//package com.prosilion.superconductor.plugin.tag;
//
//import com.prosilion.superconductor.dto.standard.VoteTagDto;
//import com.prosilion.superconductor.entity.join.standard.EventEntityVoteTagEntity;
//import com.prosilion.superconductor.entity.standard.VoteTagEntity;
//import com.prosilion.superconductor.repository.join.standard.EventEntityVoteTagEntityRepository;
//import com.prosilion.superconductor.repository.standard.VoteTagEntityRepository;
//import org.springframework.lang.NonNull;
//import com.prosilion.nostr.tag.VoteTag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class VoteTagPlugin<
//    P extends VoteTag,
//    Q extends VoteTagEntityRepository<R>,
//    R extends VoteTagEntity,
//    S extends EventEntityVoteTagEntity,
//    T extends EventEntityVoteTagEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {
//
//  @Autowired
//  public VoteTagPlugin(@NonNull VoteTagEntityRepository<R> repo, @NonNull EventEntityVoteTagEntityRepository<S> join) {
//    super(repo, join, "v");
//  }
//
//  @Override
//  public VoteTagDto getTagDto(@NonNull P voteTag) {
//    return new VoteTagDto(voteTag);
//  }
//
//  @Override
//  public S getEventEntityTagEntity(@NonNull Long eventId, @NonNull Long voteTagId) {
//    return (S) new EventEntityVoteTagEntity(eventId, voteTagId);
//  }
//}
