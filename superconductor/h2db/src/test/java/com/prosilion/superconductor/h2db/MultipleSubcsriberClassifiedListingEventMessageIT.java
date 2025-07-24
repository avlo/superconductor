package com.prosilion.superconductor.h2db;

import com.prosilion.superconductor.h2db.util.Factory;
import com.prosilion.superconductor.h2db.util.NostrRelayService;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Nested
class MultipleSubcsriberClassifiedListingEventMessageIT extends AbstractMultipleSubscriber {
  private final String authorPubKey;
  private final String content;
  private final String title;
  private final String summary;

  private final String pubKeyTagPubKey;
  private final String subject;
  private final String eventTagId;
  private final String geoTagText;

  @Autowired
  MultipleSubcsriberClassifiedListingEventMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);

    this.eventTagId = Factory.generateRandomHex64String();
    this.authorPubKey = Factory.generateRandomHex64String();
    this.content = Factory.lorumIpsum(getClass());
    this.subject = Factory.lorumIpsum(getClass());
    this.title = Factory.lorumIpsum(getClass());
    this.summary = Factory.lorumIpsum(getClass());
    this.pubKeyTagPubKey = Factory.generateRandomHex64String();
    this.geoTagText = Factory.lorumIpsum(getClass());

//    unused here, but might be useful for later/other tests reference
//    List<BaseTag> tags = new ArrayList<>();
//    tags.add(E_TAG);
//    tags.add(P_TAG);
//    tags.add(SUBJECT_TAG);
//    tags.add(G_TAG);
//    tags.add(T_TAG);
//
//    GenericEventKindIF classifiedListingEvent = Factory.createClassifiedListingEvent(
//        IDENTITY,
//        tags,
//        content,
//        Factory.createClassifiedListing(
//            title,
//            summary));
  }

  public String getGlobalEventJson(String startEventId) {
    return "[\"EVENT\", {\"id\":\"" + startEventId + "\", \"kind\": 30402, \"content\": \"" + content + "\", \"tags\": [[\"subject\", \"" + subject + "\"], [\"title\", \"" + title + "\"], [\"published_at\", \"1718843251760\"], [\"summary\", \"" + summary + "\"], [\"location\", \"classified peroulades\"], [\"price\", \"271.00\", \"BTC\", \"1\"], [\"e\", \"" + eventTagId + "\"], [\"p\", \"" + pubKeyTagPubKey + "\"], [\"t\", \"classified hash-tag-1111\"], [\"g\", \"" + geoTagText + "\" ]], \"pubkey\": \"" + authorPubKey + "\", \"created_at\": 1718843251760, \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  public String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\", \"tags\": [[\"subject\", \"" + subject + "\"], [\"title\", \"" + title + "\"], [\"published_at\", \"1718843251760\"], [\"summary\", \"" + summary + "\"], [\"location\", \"classified peroulades\"], [\"price\", \"271.00\", \"BTC\", \"1\"], [\"e\", \"" + eventTagId + "\"], [\"p\", \"" + pubKeyTagPubKey + "\"], [\"t\", \"classified hash-tag-1111\"], [\"g\", \"" + geoTagText + "\" ]], \"pubkey\": \"" + authorPubKey + "\", \"created_at\": 1718843251760, \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\", \"kind\": 30402, \"content\": \"" + content + "\"}";
  }

  public String createReqJson(@NonNull String uuid) {
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }
}
