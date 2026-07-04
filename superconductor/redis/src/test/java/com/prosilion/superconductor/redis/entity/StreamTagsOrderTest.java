package com.prosilion.superconductor.redis.entity;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.BaseReqMessageOrderedATagETagPairsIT;
import com.prosilion.superconductor.util.Factory;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

public class StreamTagsOrderTest {
  String url = "ws://localhost:5555";
  List<BaseTag> tags_1;
  List<BaseTag> tags_2;
  List<BaseTag> tags_3;

  public StreamTagsOrderTest() {
    PubKeyTag pubKeyTag_1 = new PubKeyTag(Identity.generateRandomIdentity().getPublicKey());
    HashtagTag hashtagTag_1 = new HashtagTag("hashtag_1");
    IdentifierTag uuid_1 = new IdentifierTag("UUID_1");

    AddressTag addressTag_1 = new AddressTag(
       Kind.TEXT_NOTE,
       Identity.generateRandomIdentity().getPublicKey(),
       uuid_1);

    EventTag eventTag_1 = new EventTag(Factory.generateRandomHex64String(), url);
    this.tags_1 = List.of(pubKeyTag_1, addressTag_1, eventTag_1, hashtagTag_1);
    System.out.printf("tags_1 original order:\n  %s%n", getCollect(tags_1));

    PubKeyTag pubKeyTag_2 = new PubKeyTag(Identity.generateRandomIdentity().getPublicKey());
    HashtagTag hashtagTag_2 = new HashtagTag("hashtag_2");
    IdentifierTag uuid_2 = new IdentifierTag("UUID-2");

    AddressTag addressTag_2 = new AddressTag(
       Kind.TEXT_NOTE,
       Identity.generateRandomIdentity().getPublicKey(),
       uuid_2);

    EventTag eventTag_2 = new EventTag(Factory.generateRandomHex64String(), url);
    this.tags_2 = List.of(pubKeyTag_2, addressTag_2, eventTag_2, hashtagTag_2);
    System.out.printf("tags_2 original order:\n  %s%n", getCollect(tags_2));

    PubKeyTag pubKeyTag_3 = new PubKeyTag(Identity.generateRandomIdentity().getPublicKey());
    HashtagTag hashtagTag_3 = new HashtagTag("hashtag_3");
    IdentifierTag uuid_3 = new IdentifierTag("UUID-3");

    AddressTag addressTag_3 = new AddressTag(
       Kind.TEXT_NOTE,
       Identity.generateRandomIdentity().getPublicKey(),
       uuid_3);

    EventTag eventTag_3 = new EventTag(Factory.generateRandomHex64String(), url);
    this.tags_3 = List.of(addressTag_3, eventTag_3, hashtagTag_3, pubKeyTag_3);
    System.out.printf("tags_3 original order:\n  %s%n", getCollect(tags_3));
    System.out.println();
  }

  @Test
  void equalityTest() {
    List<BaseTag> concat_1_2 = concat(tags_1, tags_2);
    System.out.printf("concat tags_1, tags_2:\n  %s%n", getCollect(concat_1_2));
    List<BaseTag> flatMappedList = flatMap(tags_1, tags_2);
    System.out.printf("flatMappedList of concat_1_2_ListOfList:\n  %s%n", getCollect(flatMappedList));
    extracted(concat_1_2, flatMappedList);
  }

  @Test
  void equalityTest_2() {
    List<BaseTag> concat_1_2 = concat(tags_1, tags_2);
    System.out.printf("concat tags_1, tags_2:\n  %s%n", getCollect(concat_1_2));
    List<BaseTag> flatMappedList = flatMap(tags_1, tags_2);
    extracted(concat_1_2, flatMappedList);
  }

  private List<BaseTag> concat(List<BaseTag> tags, List<BaseTag> tags2) {
    return Stream.concat(tags.stream(), tags2.stream()).toList();
  }

  private @NonNull String getCollect(List<BaseTag> concat_1_2) {
    return BaseReqMessageOrderedATagETagPairsIT.getTagStream(concat_1_2).collect(Collectors.joining(", "));
//    return BaseReqMessageOrderedATagETagPairsIT.getTagStream(concat_1_2).collect(Collectors.joining("\n  "));
  }

  private List<BaseTag> flatMap(List<BaseTag> tags, List<BaseTag> tags2) {
    return Stream.of(tags, tags2).toList().stream().flatMap(Collection::stream).toList();
  }

  private void extracted(List<BaseTag> list1, List<BaseTag> list2) {
    System.out.printf("      size equals? [%s]%n", list1.size() == list2.size() ? "TRUE" : "FALSE");
    System.out.printf("list order equals? [%s]%n", list1.equals(list2) ? "TRUE" : "FALSE");
  }
}
