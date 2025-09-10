package com.lumi.app.repository;

import com.lumi.app.domain.TicketFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketFile entity.
 */
@Repository
public interface TicketFileRepository extends JpaRepository<TicketFile, Long>, JpaSpecificationExecutor<TicketFile> {
    @Query("select ticketFile from TicketFile ticketFile where ticketFile.uploader.login = ?#{authentication.name}")
    List<TicketFile> findByUploaderIsCurrentUser();

    default Optional<TicketFile> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TicketFile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TicketFile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ticketFile from TicketFile ticketFile left join fetch ticketFile.ticket left join fetch ticketFile.uploader",
        countQuery = "select count(ticketFile) from TicketFile ticketFile"
    )
    Page<TicketFile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select ticketFile from TicketFile ticketFile left join fetch ticketFile.ticket left join fetch ticketFile.uploader")
    List<TicketFile> findAllWithToOneRelationships();

    @Query(
        "select ticketFile from TicketFile ticketFile left join fetch ticketFile.ticket left join fetch ticketFile.uploader where ticketFile.id =:id"
    )
    Optional<TicketFile> findOneWithToOneRelationships(@Param("id") Long id);
}
