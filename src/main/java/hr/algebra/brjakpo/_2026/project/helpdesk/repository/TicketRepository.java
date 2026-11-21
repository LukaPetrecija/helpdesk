package hr.algebra.brjakpo._2026.project.helpdesk.repository;

import hr.algebra.brjakpo._2026.project.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Ticket list: loads requester, assignee and category in the same query
     * (one SELECT with joins instead of 1 + N extra SELECTs).
     */
    @EntityGraph(attributePaths = {"requester", "assignee", "category"})
    List<Ticket> findAllByOrderByCreatedAtDesc();

    /** Ticket detail: everything the detail screen shows, including comments and their authors. */
    @EntityGraph(attributePaths = {"requester", "assignee", "category", "team", "slaPolicy",
            "comments", "comments.author"})
    @Query("select t from Ticket t where t.id = :id")
    Optional<Ticket> findWithDetailsById(@Param("id") Long id);
}
