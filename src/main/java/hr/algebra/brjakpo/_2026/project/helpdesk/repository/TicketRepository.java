package hr.algebra.brjakpo._2026.project.helpdesk.repository;

import hr.algebra.brjakpo._2026.project.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByOrderByCreatedAtDesc();
}
