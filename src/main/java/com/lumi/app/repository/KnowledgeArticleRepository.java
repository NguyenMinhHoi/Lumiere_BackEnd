package com.lumi.app.repository;

import com.lumi.app.domain.KnowledgeArticle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the KnowledgeArticle entity.
 *
 * When extending this class, extend KnowledgeArticleRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface KnowledgeArticleRepository
    extends
        KnowledgeArticleRepositoryWithBagRelationships, JpaRepository<KnowledgeArticle, Long>, JpaSpecificationExecutor<KnowledgeArticle> {
    default Optional<KnowledgeArticle> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<KnowledgeArticle> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<KnowledgeArticle> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select knowledgeArticle from KnowledgeArticle knowledgeArticle left join fetch knowledgeArticle.category",
        countQuery = "select count(knowledgeArticle) from KnowledgeArticle knowledgeArticle"
    )
    Page<KnowledgeArticle> findAllWithToOneRelationships(Pageable pageable);

    @Query("select knowledgeArticle from KnowledgeArticle knowledgeArticle left join fetch knowledgeArticle.category")
    List<KnowledgeArticle> findAllWithToOneRelationships();

    @Query(
        "select knowledgeArticle from KnowledgeArticle knowledgeArticle left join fetch knowledgeArticle.category where knowledgeArticle.id =:id"
    )
    Optional<KnowledgeArticle> findOneWithToOneRelationships(@Param("id") Long id);
}
