package com.prosilion.superconductor.util;

import lombok.Getter;
import nostr.api.factory.impl.NIP01Impl;
import nostr.event.BaseTag;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.tag.*;
import nostr.id.Identity;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Factory {

  public static Identity createNewIdentity() {
    return Identity.generateRandomIdentity();
  }

  public static <T extends GenericEvent> T createTextNoteEvent(Identity identity, List<BaseTag> tags, String content) {
    TextNoteEvent textNoteEvent = new NIP01Impl.TextNoteEventFactory(identity, tags, content).create();
//    NIP01<NIP01Event> nip01_1 = new NIP01<>(identity);
//    EventNostr sign = nip01_1.createTextNoteEvent(tags, content).sign();
//    return sign;
    return (T) textNoteEvent;
  }

  public static GenericEvent createGenericEvent() {
    String concat = generateRandomHex64String();
    return new GenericEvent(concat.substring(0, 64));
  }

  public static String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replace("-", "");
  }

  public static <T> SubjectTag createSubjectTag(Class<T> clazz) {
    return new SubjectTag(clazz.getName() + " Subject Tag");
  }

  public static PubKeyTag createPubKeyTag(Identity identity) {
    return new PubKeyTag(identity.getPublicKey());
  }

  public static <T> GeohashTag createGeohashTag(Class<T> clazz) {
    return new GeohashTag(clazz.getName() + " Geohash Tag");
  }

  public static <T> HashtagTag createHashtagTag(Class<T> clazz) {
    return new HashtagTag(clazz.getName() + " Hashtag Tag");
  }

  public static <T> EventTag createEventTag(Class<T> clazz) {
    return new EventTag(createGenericEvent().getId());
  }

  public static PriceTag createPriceTag() {
    Factory.PriceComposite pc = new Factory.PriceComposite();
    BigDecimal NUMBER = pc.getPrice();
    String CURRENCY = pc.getCurrency();
    String FREQUENCY = pc.getFrequency();
    return new PriceTag(NUMBER, CURRENCY, FREQUENCY);
  }

  public static <T> String lorumIpsum() {
    return lorumIpsum(Factory.class);
  }

  public static <T> String lorumIpsum(Class<T> clazz) {
    return lorumIpsum(clazz, 64);
  }

  public static <T> String lorumIpsum(Class<T> clazz, int length) {
    return lorumIpsum(clazz.getSimpleName(), length);
  }

  public static <T> String lorumIpsum(String s, int length) {
    boolean useLetters = false;
    boolean useNumbers = true;
    String random = String.join("-", s, RandomStringUtils.random(length, useLetters, useNumbers));
    return random.length() > 64 ? random.substring(0, 64) : random;
  }

  public static BigDecimal createRandomBigDecimal() {
    Random rand = new Random();
    int max = 100, min = 50;
    int i = rand.nextInt(max - min + 1) + min;
    int j = (rand.nextInt(max - min + 1) + min);
    return new BigDecimal(String.valueOf(i) + '.' + j);
  }

  @Getter
  private static class PriceComposite {
    private final String currency = "BTC";
    private final String frequency = "nanosecond";
    private final BigDecimal price;

    private PriceComposite() {
      price = createRandomBigDecimal();
    }
  }
}
