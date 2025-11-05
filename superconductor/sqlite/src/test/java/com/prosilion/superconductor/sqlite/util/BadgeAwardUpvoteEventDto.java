//package com.prosilion.superconductor.sqlite.util;
//
//import com.prosilion.nostr.event.BadgeDefinitionAwardEvent;
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.user.Identity;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.superconductor.base.dto.BadgeAwardAbstractEventDto;
//import java.util.List;
//import org.springframework.lang.NonNull;
//
//public class BadgeAwardUpvoteEventDto extends BadgeAwardAbstractEventDto {
//  public BadgeAwardUpvoteEventDto(
//      @NonNull Identity authorIdentity,
//      @NonNull PublicKey upvotedUser,
//      @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent) {
//    this(authorIdentity, upvotedUser, badgeDefinitionUpvoteEvent, List.of());
//  }
//
//  public BadgeAwardUpvoteEventDto(
//      @NonNull Identity authorIdentity,
//      @NonNull PublicKey upvotedUser,
//      @NonNull BadgeDefinitionAwardEvent badgeDefinitionUpvoteEvent,
//      @NonNull List<BaseTag> tags) {
//    super(
//        authorIdentity,
//        new Vote(
//            upvotedUser,
//            badgeDefinitionUpvoteEvent).getAwardEvent(),
//        tags,
//        badgeDefinitionUpvoteEvent.getContent());
//  }
//}
