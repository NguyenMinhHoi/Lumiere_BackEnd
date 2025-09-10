package com.lumi.app.repository;

import com.lumi.app.domain.ChannelMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChannelMessage entity.
 */
@Repository
public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {
    @Query("select channelMessage from ChannelMessage channelMessage where channelMessage.author.login = ?#{authentication.name}")
    List<ChannelMessage> findByAuthorIsCurrentUser();

    default Optional<ChannelMessage> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ChannelMessage> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ChannelMessage> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select channelMessage from ChannelMessage channelMessage left join fetch channelMessage.ticket left join fetch channelMessage.author",
        countQuery = "select count(channelMessage) from ChannelMessage channelMessage"
    )
    Page<ChannelMessage> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select channelMessage from ChannelMessage channelMessage left join fetch channelMessage.ticket left join fetch channelMessage.author"
    )
    List<ChannelMessage> findAllWithToOneRelationships();

    @Query(
        "select channelMessage from ChannelMessage channelMessage left join fetch channelMessage.ticket left join fetch channelMessage.author where channelMessage.id =:id"
    )
    Optional<ChannelMessage> findOneWithToOneRelationships(@Param("id") Long id);
}
