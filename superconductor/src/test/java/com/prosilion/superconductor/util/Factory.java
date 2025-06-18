package com.prosilion.superconductor.util;

import com.prosilion.nostr.enums.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.TextNoteEvent;
import com.prosilion.nostr.event.internal.ClassifiedListing;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.GeohashTag;
import com.prosilion.nostr.tag.HashtagTag;
import com.prosilion.nostr.tag.PriceTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.SubjectTag;
import com.prosilion.nostr.user.Identity;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;

public class Factory {

  public static EventIF createEvent() throws NostrException, NoSuchAlgorithmException {
    return new TextNoteEvent(createNewIdentity(), lorumIpsum());
  }

  public static Identity createNewIdentity() {
    return Identity.generateRandomIdentity();
  }

  public static GenericEventId createGenericEventId() {
    String concat = generateRandomHex64String();
    return new GenericEventId(concat.substring(0, 64));
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
    return new EventTag(createGenericEventId().getId());
  }

  public static PriceTag createPriceTag() {
    PriceComposite pc = new PriceComposite();
    BigDecimal NUMBER = pc.getPrice();
    String CURRENCY = pc.getCurrency();
    String FREQUENCY = pc.getFrequency();
    return new PriceTag(NUMBER, CURRENCY, FREQUENCY);
  }

  public static ClassifiedListing createClassifiedListing(String title, String summary) {
    return new ClassifiedListingComposite(title, summary, createPriceTag()).getClassifiedListing();
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
    return cullStringLength(
        String.join("-", s, generateRandomAlphaNumericString(length, useLetters, useNumbers))
        , 64);
  }

  public static String lnUrl() {
//  lnurl1dp68gurn8ghj7um5v93kketj9ehx2amn9uh8wetvdskkkmn0wahz7mrww4excup0dajx2mrv92x9xp
//  match lnUrl string length of 84
    return cullStringLength("lnurl" + generateRandomHex64String(), 84);
  }

  public static String cullStringLength(String s, int x) {
    return s.length() > x ? s.substring(0, x) : s;
  }

  private static String generateRandomAlphaNumericString(int length, boolean useLetters, boolean useNumbers) {
    return RandomStringUtils.random(length, useLetters, useNumbers);
  }

  public static String generateRandomHex64String() {
    return UUID.randomUUID().toString().concat(UUID.randomUUID().toString()).replaceAll("[^A-Za-z0-9]", "");
  }

  public static BigDecimal createRandomBigDecimal() {
    Random rand = new Random();
    int max = 100, min = 50;
    int i = rand.nextInt(max - min + 1) + min;
    int j = (rand.nextInt(max - min + 1) + min);
    return new BigDecimal(String.valueOf(i) + '.' + j);
  }

  @Getter
  public static class PriceComposite {
    private final String currency = "BTC";
    private final String frequency = "nanosecond";
    private final BigDecimal price;

    private PriceComposite() {
      price = createRandomBigDecimal();
    }
  }

  @Getter
  public static class ClassifiedListingComposite {
    private final ClassifiedListing classifiedListing;

    private ClassifiedListingComposite(String title, String summary, PriceTag priceTag) {
      this.classifiedListing = new ClassifiedListing(title, summary, priceTag);
    }
  }
}
