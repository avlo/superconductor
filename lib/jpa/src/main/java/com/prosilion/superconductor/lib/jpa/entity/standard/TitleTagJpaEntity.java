//package com.prosilion.superconductor.lib.jpa.entity.standard;
//
//import com.prosilion.nostr.tag.BaseTag;
//import com.prosilion.nostr.tag.TitleTag;
//import com.prosilion.superconductor.lib.jpa.entity.AbstractTagJpaEntity;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//import jakarta.persistence.Transient;
//import java.util.List;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.NonNull;
//import lombok.Setter;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "title_tag")
//public class TitleTagJpaEntity extends AbstractTagJpaEntity {
//  private String title;
//
//  public TitleTagJpaEntity(@NonNull String title) {
//    super("title");
//    this.title = title;
//  }
//
//  @Override
//  @Transient
//  public BaseTag getAsBaseTag() {
//    return new TitleTag(title);
//  }
//
//  @Override
//  @Transient
//  public List<String> get() {
//    return List.of(title);
//  }
//}
