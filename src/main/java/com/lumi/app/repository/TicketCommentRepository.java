package com.lumi.app.repository;

import com.lumi.app.domain.TicketComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TicketComment entity.
 */
@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    @Query("select ticketComment from TicketComment ticketComment where ticketComment.author.login = ?#{authentication.name}")
    List<TicketComment> findByAuthorIsCurrentUser();

    default Optional<TicketComment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TicketComment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TicketComment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ticketComment from TicketComment ticketComment left join fetch ticketComment.ticket left join fetch ticketComment.author",
        countQuery = "select count(ticketComment) from TicketComment ticketComment"
    )
    Page<TicketComment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select ticketComment from TicketComment ticketComment left join fetch ticketComment.ticket left join fetch ticketComment.author"
    )
    List<TicketComment> findAllWithToOneRelationships();

    @Query(
        "select ticketComment from TicketComment ticketComment left join fetch ticketComment.ticket left join fetch ticketComment.author where ticketComment.id =:id"
    )
    Optional<TicketComment> findOneWithToOneRelationships(@Param("id") Long id);
}
