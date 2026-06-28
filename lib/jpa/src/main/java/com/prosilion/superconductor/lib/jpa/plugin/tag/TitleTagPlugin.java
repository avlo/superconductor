//package com.prosilion.superconductor.lib.jpa.plugin.tag;
//
//import com.prosilion.nostr.tag.TitleTag;
//import com.prosilion.superconductor.lib.jpa.entity.join.standard.EventEntityTitleTagJpaEntity;
//import com.prosilion.superconductor.lib.jpa.entity.standard.TitleTagJpaEntity;
//import com.prosilion.superconductor.lib.jpa.repository.join.standard.EventEntityTitleTagJpaEntityRepository;
//import com.prosilion.superconductor.lib.jpa.repository.standard.TitleTagJpaEntityRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TitleTagPlugin<
//   P extends TitleTag,
//   Q extends TitleTagJpaEntityRepository<R>,
//   R extends TitleTagJpaEntity,
//   S extends EventEntityTitleTagJpaEntity,
//   T extends EventEntityTitleTagJpaEntityRepository<S>> extends AbstractTagPlugin<P, Q, R, S, T> {
//
//  @Autowired
//  public TitleTagPlugin(TitleTagJpaEntityRepository<R> repo, EventEntityTitleTagJpaEntityRepository<S> join) {
//    super(repo, join, "title",
//       tag -> (R) new TitleTagJpaEntity(tag.getTitle().chars().limit(80)
//          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//          .toString()),
//       (eid, tid) -> (S) new EventEntityTitleTagJpaEntity(eid, tid));
//  }
//}
