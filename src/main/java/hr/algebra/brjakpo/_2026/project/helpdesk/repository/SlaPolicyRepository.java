package hr.algebra.brjakpo._2026.project.helpdesk.repository;

import hr.algebra.brjakpo._2026.project.helpdesk.model.SlaPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import hr.algebra.brjakpo._2026.project.helpdesk.model.Priority;

import java.util.Optional;

public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {

    Optional<SlaPolicy> findByPriority(Priority priority);
}
