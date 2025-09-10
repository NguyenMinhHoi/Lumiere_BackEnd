package com.lumi.app.repository;

import com.lumi.app.domain.Supplement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Supplement entity.
 */
@Repository
public interface SupplementRepository extends JpaRepository<Supplement, Long>, JpaSpecificationExecutor<Supplement> {
    default Optional<Supplement> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Supplement> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Supplement> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select supplement from Supplement supplement left join fetch supplement.product left join fetch supplement.supplier",
        countQuery = "select count(supplement) from Supplement supplement"
    )
    Page<Supplement> findAllWithToOneRelationships(Pageable pageable);

    @Query("select supplement from Supplement supplement left join fetch supplement.product left join fetch supplement.supplier")
    List<Supplement> findAllWithToOneRelationships();

    @Query(
        "select supplement from Supplement supplement left join fetch supplement.product left join fetch supplement.supplier where supplement.id =:id"
    )
    Optional<Supplement> findOneWithToOneRelationships(@Param("id") Long id);
}
